package dk.sdu.se_f22.productmodule.infrastructure.domain;

import dk.sdu.se_f22.brandmodule.stemming.Stemmer;
import dk.sdu.se_f22.productmodule.infrastructure.ProductIndexInfrastructure;
import dk.sdu.se_f22.productmodule.infrastructure.data.TokenParameter;
import dk.sdu.se_f22.productmodule.irregularwords.IrregularWords;
import dk.sdu.se_f22.productmodule.management.ProductAttribute;
import dk.sdu.se_f22.productmodule.management.BaseProduct;
import dk.sdu.se_f22.sharedlibrary.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ProductInfIndexImpl implements ProductInfIndex{

    @Override
    public void indexProducts(List<BaseProduct> baseProducts) {
        if (baseProducts == null) return; // This should never be here, but this is due to the ProductManager#backgroundUpdate sends a null object, instead of just giving an empty list.
        for (BaseProduct baseProduct : baseProducts) {
            List<String> tokenizedProduct = tokenize(baseProduct);
            List<String> filteredTokens = tokenFilter(tokenizedProduct);

            //PIM3.indexProducts(filteredTokens, new Product(baseProduct))
        }
    }

    private List<String> tokenFilter(List<String> tokens){
        tokens = new Stemmer().stem(tokens);
        tokens = IrregularWords.INSTANCE.searchForIrregularWords(tokens);
        //tokens = CMS.filter(tokens);
        return tokens;
    }

    private List<String> tokenize(BaseProduct baseProduct){
        TokenParameter tokenParameter = ProductIndexInfrastructure.getInstance().getTokenParameter();
        List<String> tokens = this.extractData(baseProduct, tokenParameter.getDelimiter());
        for (String token : tokens){
             for (String ignoredChar : tokenParameter.getIgnoredChars()){
                 if(token.contains(ignoredChar)){
                     token = token.replaceAll(Pattern.quote(ignoredChar), "");
                 }
             }
        }
        return tokens;
    }

    private List<String> extractData(BaseProduct baseProduct, String delimiter) {
        List<String> productData = new ArrayList<>();
        for (ProductAttribute attr : ProductAttribute.values()) {
            String data = baseProduct.get(attr);
            if (data != null && !data.equalsIgnoreCase("unavailable")) {
                productData.addAll(List.of(data.split(delimiter)));
            }
        }
        productData.addAll(baseProduct.getLocations());
        return productData;
    }
}

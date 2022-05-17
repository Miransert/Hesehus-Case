package dk.sdu.se_f22.contentmodule.infrastructure.domain.Indexing;

import dk.sdu.se_f22.contentmodule.infrastructure.data.Database;
import dk.sdu.se_f22.sharedlibrary.db.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ContentInfrastructure implements IContentInfrastructre{
    //ClassCallOutgoing CMSIndexModule = new ClassCallOutgoing;
    HTMLParser parser = new HTMLParser();
    Tokenizer tokenizer = new Tokenizer();
    FilteredTokens filterTokens = new FilteredTokens();

    @Override
    public void createHTMLSite(int htmlId, String htmlCode) throws IOException {
        

        HTMLSite newSite = new HTMLSite(htmlId, htmlCode);
        
        try (Connection connection = DBConnection.getPooledConnection()) {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO cms_htmlpages (html_id) VALUES (?)");
            stmt.setInt(1, htmlId);
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            // TODO: Handle exception   
        }

        parser.parseHTML(newSite);
        tokenizer.tokenizeHTMLBodyText(newSite);
        filterTokens.siteWithFilteredTokens(newSite);

        //CMSIndexModule.index(newSite.getFilteredTokensArray(), newSite.getId());
    }

    //When an htmlpage is updated
    @Override
    public void updateHTMLSite(int htmlId, String htmlCode) throws IOException {
        HTMLSite newSite = new HTMLSite(htmlId, htmlCode);
        database.setupDatabase();
        database.executeQuery("DELETE FROM cms_htmlpages WHERE html_id = ("+htmlId+")");
        database.executeQuery("INSERT INTO cms_htmlpages (html_id) VALUES ("+htmlId+")");
        parser.parseHTML(newSite);
        tokenizer.tokenizeHTMLBodyText(newSite);
        filterTokens.siteWithFilteredTokens(newSite);

        //CMSIndexModule.updateSingular(newSite.getFilteredTokens(), newSite.getId());
    }

    @Override
    public void deleteHTMLSite(int htmlId) {
        database.setupDatabase();
        database.executeQuery("DELETE FROM cms_htmlpages WHERE html_id = ("+htmlId+")");
        //CMSIndexModule.deleteSingular(htmlId);

    }
}
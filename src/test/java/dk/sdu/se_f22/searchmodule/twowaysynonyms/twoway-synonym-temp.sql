CREATE TABLE twoway_synonym_temp(
	uuid UUID PRIMARY KEY,
	synonym VARCHAR(55) NOT NULL UNIQUE,
	group_id INT GENERATED BY DEFAULT AS IDENTITY
);



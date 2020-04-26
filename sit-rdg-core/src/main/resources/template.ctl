OPTIONS(SKIP=1,ERRORS=0,ROWS=10000)
LOAD DATA
INFILE '${inFile}'
BADFILE '${badFile}'
APPEND
PRESERVE BLANKS
INTO TABLE ${tableName}
FIELDS TERMINATED BY ","
TRAILING NULLCOLS
(
${columnNames}
)
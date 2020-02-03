-- テーブルの和名のコメント1
SELECT quote.NumberColumn, quote.VarcharColumn1, quote.VarcharColumn2 FROM CamelCaseTable camel, DoubleQuotedTable quote WHERE camel.NumberColumn = quote.NumberColumn AND camel.VarcharColumn2 = quote.VarcharColumn2;
SELECT snake.NUMBER_COLUMN FROM CamelCaseTable camel, SNAKE_CASE_TABLE snake WHERE snake.NUMBER_COLUMN = quote.NumberColumn;
# Random Data Generator

## How to Use


```bash
java -jar sit-rdg-core-xxx.jar <command> <options>
```


```
command:

  read-sql
      Read * .sql file under the input directory and generate schema.json file in the input directory.

  gen-data
      Read schema.json file under input directory and generate * .csv file in output directory.

options:

  --input: Input directory path, default is input.
  
  --output: Output directory path, default is output.
```



# UniTo - LFT - Progetto di laboratorio - 2019/2020
## Section 01
### Run an exercise
```shell
$ cd sect01
$ make ex{01..}
```
### Clean a section
```shell
$ cd sect01
$ make clean
```

## Sections 02 to 05
### Run an exercise
```shell
$ cd sect{02..05}
$ cd ex{01..}
$ make [SRC="path/to/source.txt"]
```
If no `SRC` argument is passed, the source text file that within the exercise directory will be used.
### Clean a section
```shell
$ cd sect{02..05}
$ cd ex{01..}
$ make clean
```

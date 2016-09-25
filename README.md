
TODO: general usage informations

## Converting questions (on linux)

1.  go to `Skript_Cpp` folder,
2.  run `make isenv` to create configuration (optionally with year `make isenv
    YEAR=2017`)
    *   this will create folder `isenv_$YEAR` with binary convert and makefile
        which converts all `.qdef` files to `.new.qdef` with new editor
        integrated,
3.  download all question definition (`.qdef` files) to the `isenv_$YEAR` folder.
4.  run make in `isenv_$YEAR`,
5.  you have converted questions.


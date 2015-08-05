# BackupManager 1.0
A simple backup manager made in Java

## Use
Launch by running the provided jar file, or by running 

java -jar <name of jar> [settings file]

where settings file is an optional input that is a file of type .bkpm that was made by this program.

## Current functionality
- Backup any number of files to any number of different backup locations with the ability to have unique locations for each file.

- Revert any file from the very first backup location

- Save the location and name of all the files that have previously been backed up

- Save settings as a JSON file that has an extension of .bkpm

- Can be launched from the command line

- Can take in a settings file when launched from the command line

## Planned future functionality
- Warning messages before quitting and backing up

- Ask the user for a default backup location on first launch instead of a hardcoded default

## Possible future functionality
- Runs on a specified time every day/week/month/etc.

- Can be run from the command line

- Compresses saves

## Contributing
Fork and create a pull request.

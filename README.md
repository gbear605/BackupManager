# BackupManager
A simple backup manager made in Java

# Version
Current Release: 1.0.0
In progress: 1.1.0

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

## Planned features
- Icons

- Progress bar

- All the backups should go into a new folder named after the .bkpm filename (say backups.bkpm) with the date and time appended to the end

## Possible future features
- Runs on a specified time every day/week/month/etc.

- Can be run exclusively from the command line

- Compresses saves

## Contributing
Fork and create a pull request.

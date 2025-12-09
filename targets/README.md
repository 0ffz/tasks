# Targets

Gradle projects configuring each app target. These use the Kotlin Multiplatform targets declared in the client module
but handle the target configuration via Gradle (ex. Android configuration, packaging custom desktop executables like
AppImage). The only code contained in these targets should be an entrypoint for running the program.

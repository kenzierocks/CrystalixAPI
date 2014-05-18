@echo off
echo Loading...
start "Gradle" /wait cmd.exe /c gradle build
echo Done! Grab it out of the build/libs folder!
pause
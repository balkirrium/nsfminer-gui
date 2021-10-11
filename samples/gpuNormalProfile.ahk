Process, Exist, MSIAfterburner.exe
While (!ErrorLevel) {
    Sleep,1000
	Process, Exist, MSIAfterburner.exe
}
Sleep,5000
send ^+!1
## Chapter20 in book "Windows PowerShell Cookbook"
## Chapter20. Files and Directories


##Files and Directories
$currentLocation = (Get-Location).Path

#If you are sure that the file exists, the Resolve-Path cmdlet lets you translate a relative
#path to an absolute path
$filePath = (Resolve-Path file.txt).Path

$filePath = Join-Path (Get-Location) file.txt


##20.2. Get the Files in a Directory
Get-ChildItem
Get-ChildItem *.txt
Get-ChildItem *.txt -Recurse

#To list all directories in the current directory, use the -Attributes parameter
Get-ChildItem -Attributes Directory
dir -ad

#To get information about a specific item
Get-Item test.txt

#The -Attributes parameter supports powerful filtering against all other file and directory properties
Get-ChildItem -Attributes Compressed
Get-ChildItem -Attributes !Archive
Get-ChildItem -Attributes "Hidden, ReadOnly"
Get-ChildItem -Attributes "ReadOnly + Hidden"
Get-ChildItem c:\ -Attributes "ReadOnly, Hidden + !System"


##20.3. Find All Files Modified Before a Certain Date
Get-ChildItem -Recurse | Where-Object { $_.LastWriteTime -lt "01/01/2007" }

#To find all files modified in the last 30 days
$compareDate = (Get-Date).AddDays(-30)

#The DateTime class is the administrator¡¯s favorite calendar!
[DateTime]::IsLeapYear(2008)

$daysTillChristmas = [DateTime] "December 25" - (Get-Date)
$daysTillChristmas.Days

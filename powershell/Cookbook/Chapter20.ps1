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


##20.4. Clear the Content of a File
Clear-Content test.txt


##20.5. Manage and Change the Attributes of a File
#update the ReadOnly, Hidden, or System attributes of a file.
attrib +r test.txt
attrib -s test.txt

$file = Get-Item test.txt
$file.IsReadOnly = $true

$file = Get-Item test.txt
$file.Attributes = "ReadOnly,NotContentIndexed"

$file.Attributes = "ReadOnly","System","NotContentIndexed"
$file.Mode
$file.Attributes


##20.6. Find Files That Match a Pattern
Get-ChildItem *.txt
Get-ChildItem -Filter *~2*
Get-ChildItem -Exclude *.txt

Get-ChildItem -Include *.txt -Recurse
Get-ChildItem *.txt -Recurse
Get-ChildItem -Path c:\temp\*.txt -Recurse

Get-ChildItem -Filter *.txt -Recurse
Get-ChildItem -Exclude *.txt -Recurse

Get-ChildItem | Where-Object { $_.Name -match '^KB[0-9]+\.log$' }
Get-ChildItem -Recurse | Where-Object { $_.DirectoryName -match 'Release' }
Get-ChildItem -Recurse | Where-Object { $_.FullName -match 'temp' }


##20.7. Manage Files That Include Special Characters
Get-ChildItem -LiteralPath '[My File].txt'


##20.8. Program: Get Disk Usage Information
#Example 20-2. Get-DiskUsage.ps1


##20.9. Monitor a File for Changes
Get-Content log.txt -Wait


##20.10. Get the Version of a DLL or Executable
$file = Get-Item $pshome\powershell.exe
$file.VersionInfo

Get-ChildItem $env:WINDIR |
Select -Expand VersionInfo -ErrorAction SilentlyContinue


##20.11. Program: Get the MD5 or SHA1 Hash of a File
#Example 20-3. Get-FileHash.ps1
dir | Get-FileHash


##20.12. Create a Directory
md NewDirectory


##20.13. Remove a File or Directory
Test-Path NewDirectory
Remove-Item NewDirectory


##20.14. Rename a File or Directory
Rename-Item example.txt example2.txt


##20.15. Move a File or Directory
Move-Item example.txt c:\temp\example2.txt


##20.16. Create and Map PowerShell Drives
#To create a custom drive, use the New-PSDrive cmdlet:
$myDocs = [Environment]::GetFolderPath("MyDocuments")
New-PSDrive -Name MyDocs -Root $myDocs -PSProvider FileSystem


##20.17. Access Long File and Directory Names
#Use the -Persist parameter of the New-PSDrive cmdlet to create a new drive, using the long portion of the path as the root
PS > $root = "\\server\share\some_long_directory_name"
PS > New-PSDrive -Name L -Root $root -PSProvider FileSystem -Persist
PS > Get-Item L:\some_long_file_name.txt


##20.18. Unblock a File
#Use the Unblock-File cmdlet to clear the ¡°Downloaded from the Internet¡± flag on a file
Unblock-File c:\downloads\file.zip
Get-ChildItem -Recurse | Unblock-File


##20.19. Interact with Alternate Data Streams
Get-Item .\a.zip -Stream *
Set-Content .\a.zip:MyCustomStream -Value "Hello World"
Get-Item .\a.zip -Stream *


##20.20. Program: Move or Remove a Locked File
#Example 20-4. Move-LockedFile.ps1


##20.21. Get the ACL of a File or Directory
#To retrieve the ACL of a file, use the Get-Acl cmdlet:
Get-Acl example.txt
#Example 20-5. Get-AclMisconfiguration.ps1


##20.22. Set the ACL of a File or Directory
$acl = Get-Acl example.txt
$arguments = "LEE-DESK\Guest","FullControl","Deny"
$accessRule =
New-Object System.Security.AccessControl.FileSystemAccessRule $arguments
$acl.SetAccessRule($accessRule)
$acl | Set-Acl example.txt


##20.23. Program: Add Extended File Properties to Files
#Example 20-6. Add-ExtendedFileProperties.ps1


##20.24. Program: Create a Filesystem Hard Link
#Example 20-7. New-FilesystemHardLink.ps1


##20.25. Program: Create a ZIP Archive
#Example 20-8. New-ZipFile.ps1
dir *.ps1 | New-ZipFile scripts.zip

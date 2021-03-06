## Chapter5 in book "Windows PowerShell Cookbook"

$helpContent = Get-Help Get-ChildItem | Out-String -Stream
$helpContent -match "location"

"Power[Shell]" -replace "\[Shell\]","ful"

#Split string
"a-b-c-d-e-f" -split "-c-"
 "a-b-c-d-e-f" -split "b|[d-e]"
 "a.b.c" -split '.',0,"SimpleMatch"
 
 
 #Join string
  -join ("A","B","C")
  ("A","B","C") -join "`r`n"
  
  $list = "Hello","World"
  $list -join ", "
  
  
  #Upper or Lower case
  "Hello World".ToUpper()
  "Hello World".ToLower()
  
  "quit" -eq "QUIT"
  
  
  #Trim string
  $text = " `t Test String`t `t"
  "|" + $text.Trim() + "|"
  
  "Hello World".TrimEnd(" World")
  
  
  #Format Date
  Get-Date -Date "05/09/1998 1:23 PM" -Format "dd-MM-yyyy @ hh:mm:ss"
  
  $date = [DateTime] "05/09/1998 1:23 PM"
  "{0:dd-MM-yyyy @ hh:mm:ss}" -f $date
  
  $date = [DateTime] "05/09/1998 1:23 PM"
  $date.ToString("dd-MM-yyyy @ hh:mm:ss")
  
$dueDate = [DateTime] "01/01/2006"
if([DateTime]::Now -gt $dueDate)
{
"Account is now due"
}


##Generate Large Reports and Test Streams
Get-ChildItem C:\*.txt -Recurse | Out-File c:\temp\AllTextFiles.txt

$output = New-Object System.Text.StringBuilder
Get-ChildItem C:\*.txt -Recurse | Foreach-Object { [void] $output.AppendLine($_.FullName) }
$output.ToString()


#Formatting operator(-f)
$template = "<TR> <TD>{0}</TD> <TD>{1}</TD> </TR>"
$template -f "Name","Value"

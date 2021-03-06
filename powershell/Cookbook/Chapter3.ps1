## Chapter3 in book "Windows PowerShell Cookbook"

##Format-List
Get-Process PowerShell | Format-List
#Format-Table
Get-Process | Format-Table Name,WS
Get-Process | Format-Table | Sort Name

##Environment Variable
Get-Content variable:ErrorActionPreference
Get-Content env:systemroot

## Chapter10 in book "Windows PowerShell Cookbook"
## Chapter10. Structured Files

##10.1. Access Information in an XML File
Invoke-WebRequest blogs.msdn.com/b/powershell/rss.aspx -OutFile powershell_blog.xml
$xml = [xml] (Get-Content powershell_blog.xml)
$xml
$xml.rss
($xml.rss.channel.item).Count
($xml.rss.channel.item)[0]
($xml.rss.channel.item)[0].title
#Sorting and filtering items in an XML document
$xml.rss.channel.item | Sort-Object title | Select-Object title


##10.2. Perform an XPath Query Against XML

##10.3. Convert Objects to XML
$xml = Get-Process | ConvertTo-Xml
$xml | Select-Xml '//Property[@Name = "Name"]' | Select -Expand Node

##10.4. Modify Data in an XML File


##10.5. Easily Import and Export Your Structured Data


##10.6. Store the Output of a Command in a CSV or Delimited File
Get-Process | Export-Csv c:\temp\processes.csv


##10.7. Import CSV and Delimited Data from a File
$header = "Date","Time","PID","TID","Component","Text"
$log = Import-Csv $env:WINDIR\WindowsUpdate.log -Delimiter "`t" -Header $header
$log | Group-Object Component


##10.8. Manage JSON Data Streams
$object = [PSCustomObject] @{
Name = "Lee";
Phone = "555-1212"
}
$json = ConvertTo-Json $object
$newObject = ConvertFrom-Json $json
$newObject


##10.9. Use Excel to Manage Command Output


##10.10. Parse and Interpret PowerShell Scripts
PS > $script = '$myVariable = 10'
PS > $errors = [System.Management.Automation.PSParseError[]] @()
PS > [Management.Automation.PsParser]::Tokenize($script, [ref] $errors) | Format-Table -Auto



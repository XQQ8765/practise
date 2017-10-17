## Chapter12 in book "Windows PowerShell Cookbook"
## Chapter12. Internet-Enabled Scripts


##12.1. Download a File from an FTP or Internet Site
$source = "http://www.leeholmes.com/favicon.ico"
$destination = "c:\temp\favicon.ico"
Invoke-WebRequest $source -OutFile $destination


##12.2. Upload a File to an FTP Site
PS > $source = "c:\temp\backup.zip"
PS > $destination = "ftp://site.com/users/user/backups/backup.zip"
PS > $cred = Get-Credential
PS > $wc = New-Object System.Net.WebClient
PS > $wc.Credentials = $cred
PS > $wc.UploadFile($destination, $source)
PS > $wc.Dispose()


##12.3. Download a Web Page from the Internet
PS > $source = "http://www.bing.com/search?q=sqrt(2)"
PS > $result = [string] (Invoke-WebRequest $source)

#Search-Bing.ps1
#Get-Answer.ps1


##12.4. Parse and Analyze a Web Page from the Internet
# To download a web page, and then access the ParsedHtml property
$source = "http://www.bing.com/search?q=sqrt(2)"
$result = Invoke-WebRequest $source
$resultContainer = $result.ParsedHtml.GetElementById("results_container")
$answerElement = $resultContainer.getElementsByTagName("div") | Where-Object ClassName -eq "ans" | Select -First 1
$answerElement.innerText


#To retrieve just the images, links, or input fields
$source = "http://www.bing.com/search?q=sqrt(2)"
$result = Invoke-WebRequest $source
$result.Links
$result.ParsedHtml | Get-Member


##12.5. Script a Web Application Session
#Use the Invoke-WebRequest cmdlet to download a web page, and access the -Session Variable and -WebSession parameters. 
$cred = Get-Credential
$login = Invoke-WebRequest facebook.com/login.php -SessionVariable fb
$login.Forms[0].Fields.email = $cred.UserName
$login.Forms[0].Fields.pass = $cred.GetNetworkCredential().Password
$mainPage = Invoke-WebRequest $login.Forms[0].Action `
-WebSession $fb -Body $login -Method Post
$mainPage.ParsedHtml.getElementById("notificationsCountValue").InnerText


##12.6. Program: Get-PageUrls
#Example 12-6. Get-PageUrls.ps1
./Get-PageUrls microsoft.html http://www.microsoft.com


##12.7. Interact with REST-Based Web APIs
#Example 12-7. Using Invoke-RestMethod with the StackOverflow API
$url = "https://api.stackexchange.com/2.0/questions/unanswered" +
	"?order=desc&sort=activity&tagged=powershell&pagesize=10&site=stackoverflow"
$result = Invoke-RestMethod $url
$result.Items | Foreach-Object { $_.Title; $_.Link; "" }

#Example 12-8. Searching StackOverflow for answers to a PowerShell question
#./Search-StackOverflow.ps1 upload ftp


##12.8. Connect to a Web Service
$url = "http://www.terraserver-usa.com/TerraService2.asmx"
$terraServer = New-WebserviceProxy $url -Namespace Cookbook
$place = New-Object Cookbook.Place
$place.City = "Redmond"
$place.State = "WA"
$place.Country = "USA"
$facts = $terraserver.GetPlaceFacts($place)
$facts.Center


##12.9. Export Command Output as a Web Page
$filename = "c:\temp\help.html"
$commands = Get-Command | Where { $_.CommandType -ne "Alias" }
$summary = $commands | Get-Help | Select Name,Synopsis
$summary | ConvertTo-Html | Set-Content $filename


##12.10. Send an Email
Send-MailMessage -To guide@leeholmes.com `
	-From user@example.com `
	-Subject "Hello!" `
	-Body "Hello, from another satisfied Cookbook reader!" `
	-SmtpServer mail.example.com


##12.11. Program: Monitor Website Uptimes
#Example 12-9. Testing a URI for its status and responsiveness
.\Test-Uri.ps1 bing.com


##12.12. Program: Interact with Internet Protocols
#Example 12-10. Interacting with a remote POP3 mailbox
$http = @"
  GET / HTTP/1.1
  Host:bing.com
  `n`n
"@
$http | ./Send-TcpRequest.ps1 bing.com 80

## Chapter18 in book "Windows PowerShell Cookbook"
## Chapter18. Security and Script Signing


##18.1. Enable Scripting Through an Execution Policy


##18.2. Disable Warnings for UNC Paths
#Example 18-1. Adding a server to the list of trusted hosts
$path = "HKLM:\SOFTWARE\Microsoft\Windows\CurrentVersion\Internet Settings\" +
"ZoneMap\Domains\server"
New-Item -Path $path | New-ItemProperty -Name File -PropertyType DWORD -Value 2


##18.3. Sign a PowerShell Script, Module, or Formatting File
#To sign the script with your standard code-signing certificate, use the SetAuthenticodeSignature cmdlet
$cert = @(Get-ChildItem cert:\CurrentUser\My -CodeSigning)[0]
Set-AuthenticodeSignature file.ps1 $cert


##18.4. Program: Create a Self-Signed Certificate
#Example 18-2. New-SelfSignedCertificate.ps1


##18.5. Manage PowerShell Security in an Enterprise


##18.6. Block Scripts by Publisher, Path, or Hash


##18.7. Verify the Digital Signature of a PowerShell Script
Get-AuthenticodeSignature .\test.ps1


##18.8. Securely Handle Sensitive Information
$secureInput = Read-Host -AsSecureString "Enter your private key"
$secureInput


##18.9. Securely Request Usernames and Passwords
$credential = Get-Credential

Example 18-4. Caching credentials in memory to improve usability
$credential = $null
if (Test-Path Variable:\Lee.Holmes.CommonScript.CachedCredential)
{
	$credential = ${GLOBAL:Lee.Holmes.CommonScript.CachedCredential}
}
${GLOBAL:Lee.Holmes.CommonScript.CachedCredential} = Get-Credential $credential
$credential = ${GLOBAL:Lee.Holmes.CommonScript.CachedCredential}


##18.10. Program: Start a Process as Another User
#Example 18-5. Start-ProcessAsUser.ps1
PS > $file = Join-Path ([Environment]::GetFolderPath("System")) certmgr.msc
PS > Start-ProcessAsUser Administrator mmc $file


##18.11. Program: Run a Temporarily Elevated Command
Get-Process | ./Invoke-ElevatedCommand.ps1 {
    $input | Where-Object { $_.Handles -gt 500 } } | Sort Handles


##18.12. Securely Store Credentials on Disk
$credential = Get-Credential
$credPath = Join-Path ((dir env:USERPROFILE).Value) mytest.credential
$credential | Export-CliXml $credPath

$credential2 = Import-CliXml $credPath


##18.13. Access User and Machine Certificates
#Example 18-7. Exploring certificates in the certificate provider
Set-Location cert:\CurrentUser\
$cert = Get-ChildItem -Rec -CodeSign
$cert | Format-List


##18.14. Program: Search the Certificate Store
#Example 18-8. Search-CertificateStore.ps1
./Search-CertificateStore "Encrypting File System"


##18.15. Add and Remove Certificates
PS Cert:\CurrentUser\My > dir | Where Subject -like "*OU=Created by http://www.fiddler2.com" | Remove-Item

#Example 18-9. Adding certificates
## Adding a certificate from disk
$cert = Get-PfxCertificate <path_to_certificate>
$store = New-Object System.Security.Cryptography.X509Certificates.X509Store `
"TrustedPublisher","CurrentUser"
$store.Open("ReadWrite")
$store.Add($cert)
$store.Close()


##18.16. Manage Security Descriptors in SDDL Form
#Example 18-10. Automating security configuration of the PowerShell Remoting Users group

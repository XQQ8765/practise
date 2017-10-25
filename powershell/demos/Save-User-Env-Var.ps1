<#
Save and restore user env variables
See:
https://blogs.technet.microsoft.com/heyscriptingguy/2011/05/15/simplify-your-powershell-script-with-parameter-validation/
https://kevinmarquette.github.io/2016-10-28-powershell-everything-you-wanted-to-know-about-pscustomobject/#enumerating-property-names
#>
param(
    [Parameter(Mandatory = $true)]
    [ValidateSet("backup", "restore")]
    [String] $action
)

$username = (dir ENV:username).Value
$backup_file = Join-Path "d:\tmp" ('backup_{0}_env.json' -f $username)

switch($action) {
    "backup" {
        Write-Host "----------------Backup User env vars start"
        $vars = [Environment]::GetEnvironmentVariables("User")
        if ($vars.Count -lt 1) {
            return
        }

        $jsonstr = $vars | ConvertTo-Json
        $jsonstr | Out-File $backup_file -Encoding "UTF8"
        Write-Host "Backup User env variables into file:" $backup_file ", Content: " $jsonstr

        Write-Host "----------------Backup User env vars complete"
    }


    "restore" {
        Write-Host "----------------Restore User env vars start"
        $jsonstr = cat $backup_file | Out-String

        Write-Host "Restore User env variables from file:" $backup_file ", Content: " $jsonstr

        #customObj is with type 'PSCustomObject'
        $customObj = ConvertFrom-Json $jsonstr

        #Convert PSCustomObject to Hashtable. See https://kevinmarquette.github.io/2016-10-28-powershell-everything-you-wanted-to-know-about-pscustomobject/#enumerating-property-names
        foreach( $property in $customObj.psobject.properties.name )
        {
            Write-Host "----------------restore" $property " --> " $customObj.$property
            [Environment]::SetEnvironmentVariable($property, $customObj.$property, "User")
        }
        Write-Host "----------------Restore User env vars complete"
    }
}
<#
See: 
http://www.computerperformance.co.uk/powershell/powershell_function_shortcut.htm
http://powershellblogger.com/2016/01/create-shortcuts-lnk-or-url-files-with-powershell
http://windowsitpro.com/powershell/working-shortcuts-windows-powershell

How to use:
./Search_And_Run_App.ps1 notepad, vim
#>
param(
    [Parameter(Mandatory = $true)]
    [String[]] $appnames
)
$appnames

$Path = "$Env:ProgramData\Microsoft\Windows\Start Menu\Programs"
$StartMenuLnks = Get-ChildItem $Path -Recurse -Include *.lnk

$filteredLnks = New-Object System.Collections.ArrayList
ForEach($itemLnk in $StartMenuLnks) {
    ForEach($appname in $appnames) {
        if(!($appname.contains('*'))) {
            $appname = '*{0}*' -f $appname
        }
        if ($itemLnk.Name -like $appname) {
            $filteredLnks.Add($itemLnk)
        }
    }
}

if ($filteredLnks.Count -lt 1) {
    Write-Host "Can not find application by:" $appnames
    return
}
$appLnks = @()
if ($filteredLnks.Count -gt 1) {
    $appLnks = $filteredLnks | sort name -Unique | Out-GridView -Title 'Choose a program' -PassThru
} else {
    $appLnks = @($filteredLnks[0])
}

$appLnks | % {invokeApp($_)}


function invokeApp([object] $appLnk) {
    $Shell = New-Object -ComObject WScript.Shell
    $targetpath = $Shell.CreateShortcut($appLnk).targetpath
    if ([string]::IsNullOrEmpty($targetpath)) {
        Write-Host "Failed to find targetpath for app:" $appLnk
        return $null
    }
    Write-Host "targetpath is:" $targetpath
    $shortcutObj = [pscustomobject] @{
        ShortcutName = $appLnk.Name
        LinkTarget = $targetpath
    }

    Write-Host "Try to start application for app " $shortcutObj
    #Start-Process $shortcutObj.LinkTarget
    Invoke-Item $shortcutObj.LinkTarget
}
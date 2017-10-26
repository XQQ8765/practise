<#
Refer to: https://learn-powershell.net/2013/05/07/tips-on-implementing-pipeline-support/
Test pipeline parameters

How to use:
.\Test_Pipeline.ps1 notepad, excel -Verbose
'notepad', 'excel' | .\Test_Pipeline.ps1  -Verbose

To see the debug details:
Trace-Command parameterbinding {.\Test_Pipeline.ps1 notepad, excel -Verbose}  -PSHost
Trace-Command parameterbinding {'notepad', 'excel' | .\Test_Pipeline.ps1  -Verbose}  -PSHost
#>
param(
    [Parameter(Mandatory = $true, ValueFromPipeline=$True)]
    [String[]] $appnames
)
Begin {
    Write-Verbose "Initialize stuff in Begin block"
}

Process {
    Write-Verbose "Stuff in Process block to perform"
    $appnames
}

End {
    Write-Verbose "Final work in End block"
}
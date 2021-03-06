##############################################################################
##
## Select-TextOutput
##
## From Windows PowerShell Cookbook (O'Reilly)
1.23. Program: Search Formatted Output for a Pattern | 69
www.it-ebooks.info
## by Lee Holmes (http://www.leeholmes.com/guide)
##
##############################################################################
<#
.SYNOPSIS
Searches the textual output of a command for a pattern.
.EXAMPLE
PS > Get-Service | Select-TextOutput audio
Finds all references to "Audio" in the output of Get-Service
#>
param(
## The pattern to search for
$Pattern
)
## Set-StrictMode -Version 3
$input | Out-String -Stream | Select-String $pattern
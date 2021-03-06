## Chapter6 in book "Windows PowerShell Cookbook"
## Chapter6. Calculations and Math

#result: 1.5
$result = 3/2
#result: 2
$result = [int] (3/2)


[Math]::Abs(-10.6)
[Math]::Abs(-10.6)
[Math]::Sqrt(100)
[Math]::Sin( [Math]::PI / 2 )
[Math]::ASin(1)

#Working with large numbers
[Math]::Pow(12345, 123)
[BigInt]::Pow(12345, 123)

$num1 = [BigInt] "962822088399213984108510902933777372323"
$num2 = [BigInt] "986516486816816168176871687167106806788"
$num1 * $num2

#Should use BigInt type. If you don��t, PowerShell thinks that you are trying to
#provide a number of type Double (which loses data for extremely large numbers), and
#then converts that number to the big integer.
$r = 962822088399213984108510902933777372323
$r


#Use the Measure-Object cmdlet to measure these statistical properties of a list
1..10 | Measure-Object -Average -Sum

Get-ChildItem | Measure-Object -Property Length -Max -Min -Average -Sum

#To measure the textual features of a stream of objects, use the -Character, -Word, and
#-Line parameters of the Measure-Object cmdlet
Get-ChildItem > output.txt
Get-Content output.txt | Measure-Object -Character -Word -Line


$hexNumber = 0x1234
$hexNumber

[Convert]::ToString(1234, 2)
[Convert]::ToInt32("10011010010", 2)

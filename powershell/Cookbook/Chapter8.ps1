## Chapter8 in book "Windows PowerShell Cookbook"
## Chapter8. Utility Tasks

##8.1. Get the System Date and Time
PS > $date = Get-Date
PS > $date.DayOfWeek


##8.2. Measure the Duration of a Command
PS > Measure-Command { Start-Sleep -Milliseconds 337 }


##8.3. Read and Write from the Windows Clipboard


##8.4. Generate a Random Number or Object
Get-Random -Minimum 1 -Maximum 21

#Use simple pipeline input to pick a random element from a list:
PS > $suits = "Hearts","Clubs","Spades","Diamonds"
PS > $faces = (2..10)+"A","J","Q","K"
PS > $cards = foreach($suit in $suits) {
foreach($face in $faces) { "$face of $suit" } }
PS > $cards | Get-Random
PS > $cards | Get-Random

PS > 1..10 | Foreach-Object { (New-Object System.Random).Next(1, 21) }
PS > 1..10 | Foreach-Object { Get-Random -Min 1 -Max 21 }


##8.5. Program: Search the Windows Start Menu


##8.6. Program: Show Colorized Script Content



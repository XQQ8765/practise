## Chapter7 in book "Windows PowerShell Cookbook"
## Chapter7. Lists, Arrays and Hashtables

#Create an Array or List of Items
$myArray = 1,2,"Hello World"
$myArray

#create an array of a specific size, use the New-Object cmdlet
$myArray = New-Object string[] 10
$myArray[5] = "Hello"
$myArray[5]

#To create an array of a specific type, use a strongly typed collection
$list = New-Object Collections.Generic.List[Int]
$list.Add(10)
$list.Add("Hello")

#To create an array that you plan to modify frequently, use an ArrayList
PS > $myArray = New-Object System.Collections.ArrayList
PS > [void] $myArray.Add("Hello")
PS > [void] $myArray.AddRange( ("World","How","Are","You") )
PS > $myArray
PS > $myArray.RemoveAt(1)
PS > $myArray

#Create a Jagged or Multidimensional Array
$jagged = @(
    (1,2,3,4),
    (5,6,7,8))
$jagged[0][1]

#Access elements of an Array
$myArray = 1,2,"Hello World"
$myArray[1]
$myArray[1..2 + 0]


$items = Get-Process outlook,powershell,emacs,notepad
$items | Format-Table -IncludeIndex

#$null empty
[array]$foo = $null
$foo.count
$foo -eq $null

#7.4 Visit Each Element of an Array
#Foreach-Object
PS > $myArray = 1,2,3
PS > $sum = 0
PS > $myArray | Foreach-Object { $sum += $_ }
PS > $sum

#foreach
PS > $myArray = 1,2,3
PS > $sum = 0
PS > foreach($element in $myArray) { $sum += $element }
PS > $sum

#for loop
PS > $myArray = 1,2,3
PS > $sum = 0
for($counter = 0; $counter -lt $myArray.Count; $counter++) {
$sum += $myArray[$counter]
}
PS > $sum

#7.5 Sort an Array or List of Items
#Sort-Object
PS > Get-ChildItem | Sort-Object -Descending Length | Select Name,Length
PS > "Hello","World","And","PowerShell" | Sort-Object { $_.Substring(1,1) }

#-Unique
PS > $list = "Hello","World","And","PowerShell","World"
PS > $list = $list | Sort-Object -Unique
PS > $list

#[Array]::Sort
PS > $list = "Hello","World","And","PowerShell"
PS > [Array]::Sort($list)
PS > $list


##7.6. Determine Whether an Array Contains an Item
"Hello","World" -contains "Hello"
"Hello" -in "Hello","World"


##7.7. Combine Two Arrays
PS > $firstArray = "Element 1","Element 2","Element 3","Element 4"
PS > $secondArray = 1,2,3,4
PS > $result = $firstArray + $secondArray
PS > $result

PS > $array = 1,2
PS > $array = $array + 3,4
PS > $array += 3,4


##7.8. Find Items in an Array That Match a Value
PS > $array = "Item 1","Item 2","Item 3","Item 1","Item 12"
PS > $array -eq "Item 1"
PS > $array -like "*1*"
PS > $array -match "Item .."
PS > $array | Where-Object { $_.Length -gt 6 }


##7.9. Compare Two Lists
PS > $array1 = "Item 1","Item 2","Item 3","Item 1","Item 12"
PS > $array2 = "Item 1","Item 8","Item 3","Item 9","Item 12"
PS > Compare-Object $array1 $array2


##7.10. Remove Elements from an Array
PS > $array = "Item 1","Item 2","Item 3","Item 1","Item 12"
PS > $array -ne "Item 1"
PS > $array -notlike "*1*"
PS > $array -notmatch "Item .."


##7.11. Find Items in an Array Greater or Less Than a Value
PS > $array = "Item 1","Item 2","Item 3","Item 1","Item 12"
PS > $array -ge "Item 3"
PS > $array -lt "Item 3"


##7.12. Use the ArrayList Class for Advanced Array Tasks
PS > $myArray = New-Object System.Collections.ArrayList
PS > [void] $myArray.Add("Hello")
PS > [void] $myArray.AddRange( ("World","How","Are","You") )
PS > $myArray
PS > $myArray.RemoveAt(1)
PS > $myArray


##7.13. Create a Hashtable or Associative Array
PS > $myHashtable = @{ Key1 = "Value1"; "Key 2" = 1,2,3 }
PS > $myHashtable["New Item"] = 5
PS > $myHashTable

PS > $myHashtable = @{}
PS > $myHashtable["Hello"] = "World"
PS > $myHashtable.AnotherHello = "AnotherWorld"
PS > $myHashtable


##7.14. Sort a Hashtable by Key or Value
$myHashtable = @{}
$myHashtable["Hello"] = 3
$myHashtable["Ali"] = 2
$myHashtable["Alien"] = 4
$myHashtable["Duck"] = 1
$myHashtable["Hectic"] = 11
$myHashtable
$myHashtable.GetEnumerator() | Sort Name
$myHashtable.GetEnumerator() | Sort Value

#By using the [Ordered] type cast, you can create a hashtable that retains the order in which you define and add items:
$myHashtable = [Ordered] @{
    Duck = 1;
    Ali = 2;
    Hectic = 11;
    Alien = 4;
}
$myHashtable["Hello"] = 3
$myHashtable

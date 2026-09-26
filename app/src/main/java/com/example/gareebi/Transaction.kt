package com.example.gareebi


enum class TransactionType{
    DEBIT,
    CREDIT
}

data class Transaction(
    val id:String,
    val amount:Double,
    val type: TransactionType,
    val timestamp : Long,
    val description : String


)

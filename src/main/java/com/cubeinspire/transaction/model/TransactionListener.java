package com.cubeinspire.transaction.model;

import com.cubeinspire.transaction.model.RawTransactable;

public interface TransactionListener { void onChange(RawTransactable tx); }

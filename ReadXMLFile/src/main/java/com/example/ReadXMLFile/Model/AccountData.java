package com.example.ReadXMLFile.Model;

import java.util.List;

public class AccountData {
    private String xmlns;
    private String recordsCount;
    private String bankName;
    private String bankCode;
    private String destination;
    private String source;
    private String messageId;
    private List<Account> accounts;
	public String getXmlns() {
		return xmlns;
	}
	public void setXmlns(String xmlns) {
		this.xmlns = xmlns;
	}
	public String getRecordsCount() {
		return recordsCount;
	}
	public void setRecordsCount(String recordsCount) {
		this.recordsCount = recordsCount;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public List<Account> getAccounts() {
		return accounts;
	}
	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

    
}


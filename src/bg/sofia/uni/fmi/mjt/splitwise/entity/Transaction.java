package bg.sofia.uni.fmi.mjt.splitwise.entity;

import java.time.LocalDateTime;

// storage
public class Transaction implements Comparable<Transaction> {
    private TransactionType type;
    private LocalDateTime time;
    private String from;
    private Group group;
    private String reasonForPayment;
    private double amount;

    public Transaction(TransactionType type, LocalDateTime time, String from, Group group,
                       String reasonForPayment, double amount) {
        this.type = type;
        this.time = time;
        this.from = from;
        this.group = group;
        this.reasonForPayment = reasonForPayment;
        this.amount = amount;
    }

    public TransactionType type() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public LocalDateTime time() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
    public String from() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Group group() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String reason() {
        return reasonForPayment;
    }

    public void setReason(String reasonForPayment) {
        this.reasonForPayment = reasonForPayment;
    }

    public double amount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public int compareTo(Transaction other) {
        return time.compareTo(other.time);
    }
}

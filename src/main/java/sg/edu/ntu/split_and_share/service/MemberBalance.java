package sg.edu.ntu.split_and_share.service;

import java.math.BigDecimal;

// This is a custom class created to be used as temporary storage by PrimaryQueue in settleBalances() to store and manage the balance information for each member efficiently.
// There is no setter function for member as member field should be immutable.
// @Override toString() to give logger output meaningful information instead of illegible hashcodes. 
public class MemberBalance {
  private String member;
  private BigDecimal amount;

  public MemberBalance(String member, BigDecimal amount) {
    this.member = member;
    this.amount = amount;
  }

  public String getMember() {
    return member;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  @Override
  public String toString() {
    return "MemberBalance{" +
        "member='" + member + '\'' +
        ", amount=" + amount +
        '}';
  }
}

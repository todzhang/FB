package ddb.dsz.plugin.verifier;

public enum VerifierState {
   NotVerified("Not Verified", true),
   Verifying("Verifying"),
   VerifySuccess("Success", true),
   VerifyFailure("Failure", true),
   NoLog("No Log");

   String text;
   boolean verifiable = false;

   private VerifierState(String var3) {
      this.text = var3;
   }

   private VerifierState(String var3, boolean var4) {
      this.text = var3;
      this.verifiable = var4;
   }

   public String toString() {
      return this.text;
   }

   public boolean canBeVerified() {
      return this.verifiable;
   }
}

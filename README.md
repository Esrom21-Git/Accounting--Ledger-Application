Interesting Code Aspects
The most fascinating aspect of this Java application is how it implements a complete accounting system with a clean architecture and elegant data handling:
1. Immutable Transaction Class Design
The Transaction class is designed as an immutable record-style class:
javastatic final class Transaction {
    private final LocalDate date;
    private final LocalTime time;
    private final String description;
    private final String vendor;
    private final double amount;
    
    // Constructor, getters, and other methods...
}
This immutability provides thread safety and prevents accidental modifications to transaction data after creation.

    
   
![Alt text](screenShoots/Screenshot1.png)

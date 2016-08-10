package com.gpw.radar.service.parser.web.stockDetails;

public enum XlsMapping {

    DATE(0),
    STOCK_SHORT_NAME(1),
    OPEN_PRICE(4),
    MAX_PRICE(5),
    MIN_PRICE(6),
    CLOSE_PRICE(7),
    VOLUME(9),
    TRANSACTIONS_NUMBER(10);

    private final int cellNumber;

    XlsMapping(int cellNumber) {
        this.cellNumber = cellNumber;
    }

    public int getCellNumber() {
        return cellNumber;
    }
}

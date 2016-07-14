package com.gpw.radar.service.parser.web.stockDetails;

import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.service.parser.DateAndTimeParserService;
import com.gpw.radar.service.parser.web.UrlStreamsGetterService;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class GpwSiteStockDetailsParser implements StockDetailsParser {

    private final UrlStreamsGetterService urlStreamsGetterService;
    private final DateAndTimeParserService dateAndTimeParserService;
    private final List<Stock> stocks;

    public GpwSiteStockDetailsParser(final DateAndTimeParserService dateAndTimeParserService,
                                     final UrlStreamsGetterService urlStreamsGetterService,
                                     final List<Stock> stocks) {
        this.urlStreamsGetterService = urlStreamsGetterService;
        this.dateAndTimeParserService = dateAndTimeParserService;
        this.stocks = stocks;
    }

    @Override
    public List<StockDetails> parseStockDetails(LocalDate date) throws IOException {
        HSSFSheet sources = getWorkBookSource(date);
        List<StockDetails> result = new ArrayList<>();

        Iterator<Row> iterator = sources.iterator();
        while (iterator.hasNext()) {
            Row row = iterator.next();
            Optional<StockDetails> parsedStockDetails = getStockDetailsFrom(row);
            if (parsedStockDetails.isPresent()) {
                StockDetails stdt = parsedStockDetails.get();
                stdt.setDate(date);
                result.add(stdt);
            }
        }
        return result;
    }

    private HSSFSheet getWorkBookSource(LocalDate date) throws IOException {
        String parsedDate = dateAndTimeParserService.getStringFromDate(date);
        String urlSource = "https://www.gpw.pl/notowania_archiwalne?type=10&date=" + parsedDate + "&fetch.x=12&fetch.y=16";
        InputStream inputStreamData = urlStreamsGetterService.getInputStreamFromUrl(urlSource);
        HSSFWorkbook workbook = new HSSFWorkbook(inputStreamData);
        return workbook.getSheetAt(0);
    }

    private Optional<StockDetails> getStockDetailsFrom(Row row) {
        Optional<StockDetails> result = Optional.empty();
        Optional<Stock> stockFromRow = getStockFrom(row);
        if (stockFromRow.isPresent()) {
            StockDetails stockDetails = new StockDetails();
            stockDetails.setStock(stockFromRow.get());
            stockDetails.setDate(getDateFrom(row));
            stockDetails.setOpenPrice(getBigDecimalFrom(row, XlsMapping.OPEN_PRICE.getCellNumber()));
            stockDetails.setMaxPrice(getBigDecimalFrom(row, XlsMapping.MAX_PRICE.getCellNumber()));
            stockDetails.setMinPrice(getBigDecimalFrom(row, XlsMapping.MIN_PRICE.getCellNumber()));
            stockDetails.setClosePrice(getBigDecimalFrom(row, XlsMapping.CLOSE_PRICE.getCellNumber()));
            stockDetails.setVolume(getLongFrom(row, XlsMapping.VOLUME.getCellNumber()));
            stockDetails.setTransactionsNumber(getLongFrom(row, XlsMapping.TRANSACTIONS_NUMBER.getCellNumber()));
            result = Optional.of(stockDetails);
        }
        return result;
    }

    private Optional<Stock> getStockFrom(Row row) {
        String shortStockName = row.getCell(XlsMapping.STOCK_SHORT_NAME.getCellNumber()).getStringCellValue();
        Optional<Stock> stock = stocks.stream().filter(e -> e.getStockShortName().equals(shortStockName)).findAny();
        return stock;
    }

    private LocalDate getDateFrom(Row row) {
        return dateAndTimeParserService.parseLocalDateFromString(row.getCell(XlsMapping.DATE.getCellNumber()).getStringCellValue());
    }

    private BigDecimal getBigDecimalFrom(Row row, int cellNumber) {
        double value = row.getCell(cellNumber).getNumericCellValue();
        BigDecimal result = new BigDecimal(value);
        result = result.setScale(2, RoundingMode.HALF_UP);
        if (result.equals(new BigDecimal("0.00"))) {
            return getBigDecimalFrom(row, XlsMapping.CLOSE_PRICE.getCellNumber());
        }
        return result;
    }

    private Long getLongFrom(Row row, int cellNumber) {
        double value = row.getCell(cellNumber).getNumericCellValue();
        return Math.round(value);
    }
}

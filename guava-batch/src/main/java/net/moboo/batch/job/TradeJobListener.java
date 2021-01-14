package net.moboo.batch.job;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.annotation.AfterStep;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import static net.moboo.batch.job.TradeJobConfiguration.YEAR_MONTH;

public class TradeJobListener {
    @AfterStep
    public ExitStatus afterStep() {
//        if (YEAR_MONTH.isBefore(YearMonth.parse("200509", DateTimeFormatter.ofPattern("yyyyMM")))) {
        if (YEAR_MONTH.isBefore(YearMonth.now().minusMonths(6))) {
            return new ExitStatus("FINISHED");
        } else {
            YEAR_MONTH = YEAR_MONTH.minusMonths(1);
            return new ExitStatus("CONTINUE");
        }
    }
}

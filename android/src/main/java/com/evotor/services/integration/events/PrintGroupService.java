package com.evotor.services.integration.events;

import android.content.Context;
import android.os.Bundle;

import com.evotor.converter.from.js.ReceiptReader;
import com.evotor.converter.to.js.ReceiptWriter;
import com.evotor.services.integration.ReactIntegrationService;

import java.util.List;
import java.util.Map;

import ru.evotor.IBundlable;
import ru.evotor.framework.core.action.event.receipt.changes.position.SetPrintGroup;
import ru.evotor.framework.core.action.event.receipt.print_group.PrintGroupRequiredEvent;
import ru.evotor.framework.core.action.event.receipt.print_group.PrintGroupRequiredEventResult;

/**
 * Created by a.lunkov on 11.12.2017.
 */

public class PrintGroupService extends ReactIntegrationService {

    private static final String EVENT_NAME = "PRINT_GROUP_REQUIRED";

    public static void getResultReader(Map<String, IntegrationResultReader> target) {
        target.put(
                EVENT_NAME,
                new IntegrationResultReader() {
                    @Override
                    public IBundlable read(Context context, Map data) {
                        final List<SetPrintGroup> setPrintGroups = data.get("setPrintGroups") == null ?
                                null : ReceiptReader.INSTANCE.readSetPrintGroups((List) data.get("setPrintGroups"));
                        if (setPrintGroups != null) {
                            return new PrintGroupRequiredEventResult(
                                    data.get("extra") == null ?
                                            null : ReceiptReader.INSTANCE.readSetExtra((Map) data.get("extra")),
                                    setPrintGroups
                            );
                        } else {
                            return null;
                        }
                    }
                }
        );
    }

    @Override
    protected String getEventName() {
        return EVENT_NAME;
    }

    @Override
    protected String getActionName() {
        return PrintGroupRequiredEvent.NAME_SELL_RECEIPT;
    }

    @Override
    protected IntegrationEventWriter getEventWriter() {
        return new IntegrationEventWriter() {
            @Override
            public Object write(Bundle bundle) {
                final PrintGroupRequiredEvent event = PrintGroupRequiredEvent.create(bundle);
                if (event == null) {
                    return null;
                }
                return ReceiptWriter.INSTANCE.writePaymentSystem(event.getPaymentSystem());
            }
        };
    }

}

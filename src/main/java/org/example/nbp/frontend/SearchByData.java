package org.example.nbp.frontend;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.example.nbp.RatesServices;
import org.example.nbp.dto.ResponseCurrency;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Route("find-data")
public class SearchByData extends VerticalLayout {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy").withZone(ZoneId.of("UTC"));
    private final RatesServices ratesServices;
    private final DatePicker datePicker;


    private final Grid<ResponseCurrency> grid;

    private final ComboBox<String> currency;

    public SearchByData(RatesServices ratesServices) {
        this.ratesServices = ratesServices;


        grid = new Grid<>(ResponseCurrency.class, false);
        grid.addColumn(ResponseCurrency::currency).setHeader("Waluta");
        grid.addColumn(ResponseCurrency::value).setHeader("Kurs");
        grid.addColumn(item -> formatter.format(item.date())).setHeader("Data").setSortable(true).setComparator(ResponseCurrency::currency);


        datePicker = new DatePicker();
        datePicker.setPlaceholder("Wybierz datę");
        currency = new ComboBox<>();

        currency.setPlaceholder("Wybierz walutę");
        ratesServices.findRates().subscribe(list -> getUI().ifPresent(ui -> ui.access(() -> currency.setItems(list))));


        Button findByDate = new Button("Wyszukaj dane", event -> findByCurrencyAndTime());
        Button clear = new Button("Wyczyśc pola",clearFields->clearFields());

        add(currency, datePicker, findByDate, clear,grid);
    }

    private void clearFields() {
        currency.clear();
        datePicker.clear();
    }

    private void findByCurrencyAndTime() {
        ratesServices.findByCurrencyAndTime(currency.getValue(), datePicker.getValue()).collectList().subscribe(rates -> getUI().ifPresent(ui -> ui.access(() -> grid.setItems(rates))));

    }


}

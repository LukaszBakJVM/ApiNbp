package org.example.nbp.frontend;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("")
public class IndexView extends VerticalLayout {
    public IndexView() {

        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        H1 header = new H1("📑 Kursy NBP");
        H2 header1= new H2("Codziennie aktualizowane kursy walut – dostępne od 23 marca 2025" );
        H2 header2= new H2("Umożliwiamy wyszukiwanie danych według daty, waluty lub ich kombinacji.");
        RouterLink searchAll = new RouterLink("Kliknij aby  wyszukać wszystkie kursy", Search.class);
        searchAll.getStyle().set("font-weight", "bold");
        RouterLink searchByParam = new RouterLink("Kliknij aby  wyszukać po dacie i walucie", SearchByData.class);
        searchByParam.getStyle().set("font-weight", "bold");

        Image logo = new Image("images/logo.png","index");
        logo.setSrc("images/logo.png");

        add(header,header1,header2,searchAll,searchByParam,logo);
    }
}

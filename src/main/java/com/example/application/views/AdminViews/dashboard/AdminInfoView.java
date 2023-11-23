package com.example.application.views.AdminViews.masterdetail;

import com.example.application.Entities.Event.Event;
import com.example.application.Entities.Event.detailEvent;
import com.example.application.Entities.User.User;
import com.example.application.Resources.Services.DataService;
import com.example.application.Resources.Extras.Util;
import com.example.application.views.AdminViews.Main.AdminMainView;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataGenerator;
import com.vaadin.flow.router.*;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Route(value = "student_data/moreInfo" , layout = AdminMainView.class)
@PageTitle(value = "moreInfo")
@CssImport(value = "styles/views/moreInfo.css", include = "lumo-badge")
public class AdminInfoView extends Grid<Event> implements AfterNavigationObserver, HasUrlParameter<String>, BeforeEnterObserver {

    private Chart fundraisingChart;

    private Chart monthlyAttendanceChart;

    private Grid<detailEvent> eventsGrid;

    private Board board;

    private String firstName;
    private String lastName;

    private User roboticsMember;

    private double totalOutreachEarned;
    private double totalMoneyRaised;
    private double moneyLeftToEarn;
    private int numEventsAttended;

    DataService dataService = new DataService();

    public AdminInfoView(){
        addClassName("moreInfo");
        setSizeFull();
        board = new Board();


    }

    private void initInfoChart(){
        /*Div totalEventsDiv = new Div();
        Div outreachHours = new Div();
        Div totalFundraised = new Div();

        Span OutreachSpan = new Span("Total Outreach Hours");
        Span fundSpan = new Span("Total Money Fundraised");
        Span eventSpan = new Span("Events Attended");

        OutreachSpan.getStyle().set("color" , "var(--lumo-success-color)");
        fundSpan.getStyle().set("color" , "var(--lumo-success-color)");
        eventSpan.getStyle().set("color" , "var(--lumo-success-color)");

        Span hoursQuantity = new Span("10.0");
        Span funded = new Span("590.5");
        Span eventsAtennded = new Span("5");

        VerticalLayout hoursLay = new VerticalLayout(hoursQuantity , OutreachSpan);
        hoursLay.setSpacing(false);
        //hoursLay.setPadding(false);
        hoursLay.setAlignItems(Alignment.CENTER);
        hoursLay.setJustifyContentMode(JustifyContentMode.CENTER);
        VerticalLayout fundLay = new VerticalLayout(funded , fundSpan);
        //fundLay.setPadding(false);
        fundLay.setSpacing(false);
        fundLay.setAlignItems(Alignment.CENTER);
        fundLay.setJustifyContentMode(JustifyContentMode.CENTER);
        VerticalLayout eventLay = new VerticalLayout(eventsAtennded , eventSpan);
        //eventLay.setPadding(false);
        eventLay.setSpacing(false);
        eventLay.setAlignItems(Alignment.CENTER);
        eventLay.setJustifyContentMode(JustifyContentMode.CENTER);

        hoursLay.setMargin(false);
        hoursLay.setPadding(false);

        fundLay.setMargin(false);
        fundLay.setPadding(false);

        eventLay.setMargin(false);
        eventLay.setPadding(false);

        totalEventsDiv.add(eventLay);
        totalFundraised.add(fundLay);
        outreachHours.add(hoursLay);

        totalEventsDiv.getStyle().set("background-color" , "var(--lumo-base-color)");
        totalFundraised.getStyle().set("background-color" , "var(--lumo-base-color)");
        outreachHours.getStyle().set("background-color" , "var(--lumo-base-color)");*/


        Span div = new Span();
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(new Span(String.valueOf(totalOutreachEarned)) , new Span("Total Outreach Hours"));
        //verticalLayout.setAlignItems(Alignment.CENTER);
        div.add(verticalLayout);
        div.getStyle().set("box-shadow"  , "var(--lumo-box-shadow-xs)");
        //div.getStyle().set("border-radius"  , "var(--lumo-border-radius)");
        div.getStyle().set("background-color"  , "var(--lumo-base-color)");

        Span div2 = new Span();
        VerticalLayout verticalLayout2 = new VerticalLayout();
        verticalLayout2.add(new Span(String.valueOf(totalMoneyRaised)) , new Span("Total Money Raised"));
        //verticalLayout2.setAlignItems(Alignment.CENTER);
        div2.add(verticalLayout2);
        div2.getStyle().set("box-shadow"  , "var(--lumo-box-shadow-xs)");
        //div2.getStyle().set("border-radius"  , "var(--lumo-border-radius)");
        div2.getStyle().set("background-color"  , "var(--lumo-base-color)");


        Span div3 = new Span();
        VerticalLayout verticalLayout3 = new VerticalLayout();
        verticalLayout3.add(new Span(String.valueOf(numEventsAttended)) , new Span("Events Attended"));
        //verticalLayout3.setAlignItems(Alignment.CENTER);
        div3.add(verticalLayout3);
        div3.getStyle().set("box-shadow"  , "var(--lumo-box-shadow-xs)");
        //div3.getStyle().set("border-radius"  , "var(--lumo-border-radius)");
        div3.getStyle().set("background-color"  , "var(--lumo-base-color)");


        div.setWidthFull();
        div2.setWidthFull();
        div3.setWidthFull();

        board.addRow(div , div2 , div3);

    }

    private void initFundraisingChart(List<detailEvent> events){
        fundraisingChart = new Chart(ChartType.PIE);

        Configuration configuration = fundraisingChart.getConfiguration();
        configuration.setTitle("Money raised from events");

        Tooltip tool = new Tooltip();
        tool.setValueDecimals(1);
        configuration.setTooltip(tool);

        PlotOptionsPie plotOptions = new PlotOptionsPie();
        plotOptions.setAllowPointSelect(true);
        plotOptions.setCursor(Cursor.POINTER);
        plotOptions.setShowInLegend(true);
        configuration.setPlotOptions(plotOptions);

        DataSeries dataSeries = new DataSeries();

        for(detailEvent e : events){
            if(!e.getEventType().equals("Outreach Event")) {
                dataSeries.add(new DataSeriesItem(e.getEventName(), e.getReward()));
            }
        }

        double total = 0;
        for(DataSeriesItem d : dataSeries.getData()){
            Number n = d.getY();
            String nString = n.toString();
            double sDouble = Double.parseDouble(nString);
            total += sDouble;
        }

        DataSeriesItem amountDue = new DataSeriesItem("Amount due" , moneyLeftToEarn);
        amountDue.setSliced(true);

        dataSeries.add(amountDue);

        configuration.setSeries(dataSeries);
        fundraisingChart.setVisibilityTogglingDisabled(true);
        fundraisingChart.setWidthFull();
    }

    private void initAllEvents(List<detailEvent> events){
        eventsGrid = new Grid<>();

        eventsGrid.addComponentColumn(detailEvent -> createEventCard(detailEvent));

        eventsGrid.setItems(events);
        eventsGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        eventsGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
    }

    private void initDoubleChartRow(){
        Div chart = new Div(fundraisingChart);
        chart.getStyle().set("padding" , "var(--lumo-space-s)");
        fundraisingChart.getStyle().set("box-shadow" , "var(--lumo-box-shadow-xs)");
        fundraisingChart.getStyle().set("border-radius" , "var(--lumo-border-radius)");

        Div chart2 = new Div(eventsGrid);
        chart2.getStyle().set("padding" , "var(--lumo-space-s)");
        eventsGrid.getStyle().set("box-shadow" , "var(--lumo-box-shadow-xs)");
        eventsGrid.getStyle().set("border-radius" , "var(--lumo-border-radius)");

        board.addRow(chart , chart2);
    }

    private void initMonthlyAttendedGraph(){
        monthlyAttendanceChart = new Chart();
        monthlyAttendanceChart.setHeightFull();

        Configuration conf = monthlyAttendanceChart.getConfiguration();
        conf.setTitle("Number of events attended monthly");
        conf.setSubTitle("Data for : " + firstName + " " + lastName);
        monthlyAttendanceChart.getConfiguration().getChart().setType(ChartType.COLUMN);

        List<detailEvent> usersEvents = dataService.getAllUserEventsWithRewardStatus(firstName , lastName , 1);

        ArrayList<Integer> fundraisingPerMonth = Util.getFundraisingPerMonth(usersEvents);
        ArrayList<Integer> outreachPerMonth = Util.getOutreachEventPerMonth(usersEvents);

        ListSeries listSeries1 = new ListSeries("Fundraising Events", fundraisingPerMonth.get(0), fundraisingPerMonth.get(1), fundraisingPerMonth.get(2), fundraisingPerMonth.get(3)
                , fundraisingPerMonth.get(4), fundraisingPerMonth.get(5), fundraisingPerMonth.get(6), fundraisingPerMonth.get(7)
                , fundraisingPerMonth.get(8), fundraisingPerMonth.get(9), fundraisingPerMonth.get(10), fundraisingPerMonth.get(11));


        conf.addSeries(listSeries1);

        ListSeries listSeries2 = new ListSeries("Outreach Events", outreachPerMonth.get(0), outreachPerMonth.get(1), outreachPerMonth.get(2), outreachPerMonth.get(3)
                , outreachPerMonth.get(4), outreachPerMonth.get(5), outreachPerMonth.get(6), outreachPerMonth.get(7)
                , outreachPerMonth.get(8), outreachPerMonth.get(9), outreachPerMonth.get(10), outreachPerMonth.get(11));

        PlotOptionsColumn plotOptionsColumn1 = new PlotOptionsColumn();
        plotOptionsColumn1.setColorIndex(8);

        listSeries2.setPlotOptions(plotOptionsColumn1);

        conf.addSeries(listSeries2);

        XAxis x = new XAxis();
        x.setCrosshair(new Crosshair());
        x.setCategories("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
                "Sep", "Oct", "Nov", "Dec");
        conf.addxAxis(x);

        YAxis y = new YAxis();
        y.setMin(0);
        y.setTitle("Events Attended");
        conf.addyAxis(y);
        y.setMax(15);

        Tooltip tooltip = new Tooltip();
        tooltip.setShared(true);
        conf.setTooltip(tooltip);

        Div chart = new Div(monthlyAttendanceChart);
       // chart.getStyle().set("padding" , "var(--lumo-space-s)");
        chart.getStyle().set("box-shadow" , "var(--lumo-box-shadow-xs)");
        chart.getStyle().set("border-radius" , "var(--lumo-border-radius)");
        chart.setWidthFull();

        HorizontalLayout leftArrow = new HorizontalLayout();
        HorizontalLayout rightArrow = new HorizontalLayout();

        Icon leftIcon = new Icon(VaadinIcon.ARROW_LEFT);
        Icon rightIcon = new Icon(VaadinIcon.ARROW_RIGHT);

        leftArrow.add(leftIcon);
        rightArrow.add(rightIcon);

        leftArrow.addClassName("chartArrow");
        rightArrow.addClassName("chartArrow");

        Row row = new Row( chart );
        row.setWidthFull();
        row.setHeightFull();

        board.addRow(row).getStyle().set("padding" , "var(--lumo-space-s)");
    }

    private Board createEventCard(detailEvent event) {
        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");
        card.setWidthFull();
        card.setSpacing(false);
        card.getThemeList().add("spacing-s");

        Board board = new Board();

        VerticalLayout description = new VerticalLayout();
        description.addClassName("description");
        description.setSpacing(false);
        description.setPadding(false);

        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("header");
        header.setSpacing(false);
        header.getThemeList().add("spacing-s");

        Span eventName = new Span(event.getEventName());
        eventName.addClassName("name");
        Span date = new Span(event.getStartingDate());
        date.addClassName("date");

        Span desc = new Span(event.getDescription());
        desc.addClassName("post");

        String rewardS = event.getEventType().equals("Fundraising") ? "Money Raised : " + event.getReward() : "Hours earned : " + event.getReward();

        Button reward = new Button(rewardS);
        reward.setHeight("20px");
        reward.getStyle().set("background-color" , "var(--lumo-success-color)");
        reward.getStyle().set("color" , "var(--lumo-base-color)");
        reward.getStyle().set("opacity" , "0.65");
        reward.getStyle().set("border-radius" , "1.0em");
        reward.getStyle().set("font-size" , "0.8em");

        header.add(eventName , reward);
        //header.setAlignItems(Alignment.CENTER);

        description.add(header , desc);

        HorizontalLayout hLay = new HorizontalLayout();
        hLay.add(description);
        //hLay.setAlignItems(Alignment.CENTER);

        desc.getStyle().set("padding" , "var(--lumo-space-s)");
        board.addRow(eventName);
        board.addRow(reward);
        board.addRow(desc);

        board.getStyle().set("padding" , "var(--lumo-space-s)");
        //HorizontalLayout card = new HorizontalLayout();
        card.add(hLay);
        card.setWidthFull();

        board.getStyle().set("background-color" , "var(--lumo-base-color)");
        board.getStyle().set("margin" , "0");

        return board;
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @WildcardParameter String s) {

    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
            ArrayList<Event> events = new ArrayList<>();
            events.add((new Event(null, null, null, null, 0, null, null, null, null, null, null, null, null,0, null , 0)));
            setItems(events);

            List<detailEvent> userEvents = dataService.getAllUserEventsWithRewardStatus(firstName , lastName , 1);

            initAllEvents(userEvents);
            initInfoChart();
            initMonthlyAttendedGraph();
            initFundraisingChart(userEvents);
            initDoubleChartRow();


            addComponentColumn(event -> board);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        if(authentication instanceof AnonymousAuthenticationToken){
            beforeEnterEvent.forwardTo("login");
            return;
        }

        if(beforeEnterEvent.getLocation().getSegments().size() != 3){
            if(authentication instanceof AnonymousAuthenticationToken){
                beforeEnterEvent.forwardTo("login");
                return;
            }else{
                Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
                List<String> Roles = new ArrayList<String>();

                for(GrantedAuthority a : authorities){
                    Roles.add(a.getAuthority());
                }

                if(Roles.contains("ROLE_ADMIN")){
                    beforeEnterEvent.forwardTo("student_data");
                }

                return;

            }
        }

        String fullParam = beforeEnterEvent.getLocation().getSegments().get(2);
        String[] str = fullParam.split("-");

        firstName = str[0];
        lastName = str[1];

        roboticsMember = dataService.getUserByName(firstName , lastName);

        totalOutreachEarned = roboticsMember.getTotalOutreachHoursEarned();
        totalMoneyRaised = roboticsMember.getTotalMoneyFundraised();
        moneyLeftToEarn = 600.0 - totalMoneyRaised;
        numEventsAttended = roboticsMember.getNumEventsAttended();
    }

    @Override
    public void removeDataGenerator(DataGenerator<Event> dataGenerator) {

    }
}

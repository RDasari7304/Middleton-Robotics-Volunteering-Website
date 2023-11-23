package com.example.application.views.RegUserViews;

import com.example.application.Security.LoggedInUserDetails;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import java.util.Arrays;
import java.util.Optional;

/**
 * The main view is a top-level placeholder for other views.
 */
@JsModule("./styles/shared-styles.js")
@PWA(name = "My Project", shortName = "My Project")
@Theme(value = Lumo.class, variant = Lumo.LIGHT)
@CssImport(value = "styles/views/main/main-view.css")
public class StudentMainView extends AppLayout {

    private final Tabs menu;
    private H1 viewTitle;
    Icon newIcon = new Icon(VaadinIcon.CIRCLE);
    DrawerToggle drawerToggle;

    public StudentMainView() {
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        menu = createMenu();
        menu.addThemeName("menu");
        addToDrawer(createDrawerContent(menu));
    }

    private Component createHeaderContent() {
        drawerToggle = new DrawerToggle();
        drawerToggle.addClassName("toggle");
        drawerToggle.setEnabled(false);
        viewTitle = new H1();
        viewTitle.addClassName("h1");
        Anchor logoutAnchor = new Anchor("/logout" , "Logout");
        logoutAnchor.getStyle().set("margin-left" , "auto");
        logoutAnchor.getStyle().set("margin-right" , "2vw");
        logoutAnchor.getStyle().set("margin-top" , "auto");
        logoutAnchor.getStyle().set("margin-bottom" , "auto");
        logoutAnchor.getStyle().set("color" , "#e2cd04");
        HorizontalLayout layout = new HorizontalLayout(drawerToggle, viewTitle, logoutAnchor);
        layout.setWidthFull();
        layout.setId("header");
        layout.getThemeList().set("dark", true);
        layout.expand(viewTitle);
        layout.setAlignSelf(FlexComponent.Alignment.END, logoutAnchor);
        layout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        layout.addClassName("menu");


        //layout.setWidthFull();
        //layout.setSpacing(false);
        //layout.setAlignItems(FlexComponent.Alignment.CENTER);
        /*layout.add(new Image("https://randomuser.me/api/portraits/women/8.jpg",
                "Avatar"));*/
        return layout;
    }

    private Component createDrawerContent(Tabs menu) {
        Image logo = new Image("/images/logos/oie_transparent.png", "");
        logo.setSizeFull();
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getThemeList().set("spacing-s", true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);
        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setId("logo");
        logoLayout.addClassName("menu");
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        logoLayout.add(logo);
        H1 heading = new H1("");
        heading.setClassName("h1");
        Label userLabel = new Label(LoggedInUserDetails.getFirstName() + " " +
                LoggedInUserDetails.getLastName()/*LoggedInUserDetails.getFirstName() + " " + LoggedInUserDetails.getLastName()*/);
        userLabel.getStyle().set("color", "#e2cd04");
        //userLabel.addClassName("menu");
        layout.add(logoLayout, menu, userLabel);
        layout.expand(menu);
        layout.setAlignSelf(FlexComponent.Alignment.CENTER, userLabel);
        layout.addClassName("menu");
        return layout;
    }

    private Tabs createMenu() {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.setId("tabs");
        tabs.addClassName("menu");

        Tab tab = new Tab(new RouterLink("Events" , StudentDashboardView.class));
        Tab tab3 = new Tab(new RouterLink("Statistics" , StudentMoreInfoView.class));
        Tab tab2 = new Tab(new RouterLink("Requests" , PreviousSignUpsView.class));

        tab.getStyle().set("color" , "#e2cd04");
        tab2.getStyle().set("color" , "#e2cd04");
        tab3.getStyle().set("color" , "#e2cd04");

        tabs.add(tab , tab3 , tab2);
        return tabs;
    }

    private Tabs createMenuItems() {
        Tabs tabs = new Tabs();

        Tab tab = new Tab(new RouterLink("Events" , StudentDashboardView.class));
        Tab tab2 = new Tab(new RouterLink("Previous Sign Ups" , PreviousSignUpsView.class));
        Tab tab3 = new Tab(new RouterLink("Dashboard" , StudentMoreInfoView.class));

        tab.getStyle().set("color" , "#e2cd04");
        tab2.getStyle().set("color" , "#e2cd04");
        tab3.getStyle().set("color" , "#e2cd04");

        tabs.add(tab , tab2 , tab3);
        return tabs;

        /*RouterLink events = new RouterLink("Events", StudentDashboardView.class);
        events.addClassName("events");
        RouterLink previousSignUps = new RouterLink("Previous Sign Ups", PreviousSignUpsView.class);
        RouterLink infoView = new RouterLink("Dashboard", StudentMoreInfoView.class);
        RouterLink[] links = new RouterLink[]{
                events,
                infoView,
                previousSignUps
        };
        return Arrays.stream(links).map(StudentMainView::createTab).toArray(Tab[]::new);*/
    }


    private static Tab createTab(Component content) {
        final Tab tab = new Tab();
        tab.add(content);
        return tab;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        updateChrome();
    }

    private void updateChrome() {
        getTabWithCurrentRoute().ifPresent(menu::setSelectedTab);
        viewTitle.setText(getCurrentPageTitle());
    }

    private Optional<Tab> getTabWithCurrentRoute() {
        String currentRoute = RouteConfiguration.forSessionScope()
                .getUrl(getContent().getClass());
        return menu.getChildren().filter(tab -> hasLink(tab, currentRoute))
                .findFirst().map(Tab.class::cast);
    }

    private boolean hasLink(Component tab, String currentRoute) {
        return tab.getChildren().filter(RouterLink.class::isInstance)
                .map(RouterLink.class::cast).map(RouterLink::getHref)
                .anyMatch(currentRoute::equals);
    }

    private String getCurrentPageTitle() {
        return getContent().getClass().getAnnotation(PageTitle.class).value();
    }
}

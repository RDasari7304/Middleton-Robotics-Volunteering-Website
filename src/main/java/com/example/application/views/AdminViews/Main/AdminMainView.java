package com.example.application.views.AdminViews.Main;

import com.example.application.Entities.Event.Request;
import com.example.application.Security.LoggedInUserDetails;
import com.example.application.Resources.Services.Database;
import com.example.application.views.AdminViews.dashboard.AdminDashboardView;
import com.example.application.views.AdminViews.dashboard.ApprovalView;
import com.example.application.views.AdminViews.masterdetail.AdminUsersInfoView;
import com.example.application.views.AdminViews.masterdetail.ViewUsers;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.*;
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
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import java.util.ArrayList;
import java.util.Optional;

/**
 * The main view is a top-level placeholder for other views.
 */
@JsModule("./styles/shared-styles.js")
@Theme(value = Lumo.class, variant = Lumo.LIGHT)
@CssImport("styles/views/main/main-view.css")
public class AdminMainView extends AppLayout {

    private final Tabs menu;
    private H1 viewTitle;

    public static Span pendingRequests;


    public AdminMainView() {
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        menu = createMenu();
        menu.addClassName("menu");
        addToDrawer(createDrawerContent(menu));
    }

    private Component createHeaderContent() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setId("header");
        layout.getThemeList().set("dark", true);
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        DrawerToggle drawerToggle = new DrawerToggle();
        drawerToggle.addClassName("toggle");
        drawerToggle.setEnabled(false);
        layout.add(drawerToggle);
        layout.addClassName("menu");
        viewTitle = new H1();
        layout.add(viewTitle);

        Anchor logoutAnchor = new Anchor("/logout" , "Logout");
        logoutAnchor.getStyle().set("margin-left" , "auto");
        logoutAnchor.getStyle().set("margin-right" , "2vw");
        logoutAnchor.getStyle().set("margin-top" , "auto");
        logoutAnchor.getStyle().set("margin-bottom" , "auto");
        logoutAnchor.getStyle().set("color" , "#e2cd04");

        layout.add(logoutAnchor);


        return layout;
    }

    private Component createDrawerContent(Tabs menu) {
        Image middletonRoboticsLogo = new Image("images/logos/oie_transparent.png" , "");
        middletonRoboticsLogo.setSizeFull();
        middletonRoboticsLogo.addClassName("logoPic");

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getThemeList().set("spacing-s", true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);
        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setId("logo");
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        logoLayout.add(middletonRoboticsLogo);
        logoLayout.addClickListener(e ->{
            UI.getCurrent().navigate(AdminDashboardView.class);
        });
        logoLayout.addClassName("menu");

        layout.add(logoLayout, menu);
        layout.addClassName("menu");

        Icon userIcon = new Icon(VaadinIcon.USER);
        userIcon.getStyle().set("color" , "#e2cd04");
        userIcon.getStyle().set("margin-left" , "auto");
        userIcon.getStyle().set("margin-right" , "auto");

        Span currentName = new Span(LoggedInUserDetails.getFirstName() + " " + LoggedInUserDetails.getLastName());
        currentName.getStyle().set("color" , "#e2cd04");
        currentName.getStyle().set("margin-right" , "auto");
        currentName.getStyle().set("margin-left" , "auto");
        currentName.getStyle().set("margin-bottom" , "auto");

        Span userType = new Span(LoggedInUserDetails.getRoles().contains("ROLE_ADMIN") ? "Middleton Robotics Admin" : "Middleton Robotics Member");
        userType.getStyle().set("color" , "#e2cd04");
        userType.getStyle().set("margin-left" , "auto");
        userType.getStyle().set("margin-right" , "auto");

        VerticalLayout userDisplay = new VerticalLayout(userIcon , currentName);
        userDisplay.setWidthFull();


        VerticalLayout userDetailsLayout = new VerticalLayout();
        Hr userDetailsHorizontalRule = new Hr();
        userDetailsHorizontalRule.setWidthFull();
        userDetailsHorizontalRule.setHeight("1.5%");
        userDetailsHorizontalRule.getStyle().set("color" , "#000");

        Anchor logoutAnchor = new Anchor("/logout" , "Logout");
        logoutAnchor.getStyle().set("margin-left" , "auto");
        logoutAnchor.getStyle().set("margin-right" , "auto");
        logoutAnchor.getStyle().set("color" , "#e2cd04");

        userDetailsLayout.add(userDetailsHorizontalRule , userDisplay);
        userDetailsLayout.setHeight("20%");
        userDetailsLayout.setWidthFull();
        userDetailsLayout.getStyle().set("margin-top" , "auto");

        layout.add(userDetailsLayout);
        return layout;
    }

    private Tabs createMenu() {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.setId("tabs");
        Tab tab = new Tab(new RouterLink("Events" , AdminDashboardView.class));
        Tab tab2 = new Tab(new RouterLink("Student Data" , AdminUsersInfoView.class));
        Tab tab3 = new Tab(new RouterLink("Requests" , ApprovalView.class));
        Tab tab4 = new Tab(new RouterLink("Users" , ViewUsers.class));

        ArrayList<Request> pendingRequestsArray = Database.getAllRequestsWithStatus("pending");

        if(pendingRequestsArray.size() > 0) {
            pendingRequests = new Span(String.valueOf(pendingRequestsArray.size()));
            pendingRequests.addClassName("requestIndicator");
            tab3.add(pendingRequests);
        }else{
            pendingRequests = new Span();
            pendingRequests.addClassName("requestIndicator");
            pendingRequests.setVisible(false);
            tab3.add(pendingRequests);
        }


        tab.getStyle().set("color" , "#e2cd04");
        tab2.getStyle().set("color" , "#e2cd04");
        tab3.getStyle().set("color" , "#e2cd04");
        tab4.getStyle().set("color" , "#e2cd04");

        tabs.add(tab , tab2 , tab3 , tab4);

        return tabs;
    }

    private static Tab createTab(Component content) {
        final Tab tab = new Tab();
        tab.add(content);
        tab.getStyle().set("color" , "#e2cd04");
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

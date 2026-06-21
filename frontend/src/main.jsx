import React, { useEffect, useMemo, useState } from "react";
import { createRoot } from "react-dom/client";
import {
  Archive, Bell, Building2, ClipboardList, Download, Factory, FileBarChart,
  Filter, LayoutDashboard, LogIn, Plus, RefreshCcw, Search, Settings,
  ShoppingCart, SlidersHorizontal, Truck, UserCircle, Users, WalletCards, X,
  ChevronRight, CircleDollarSign, CheckCircle2, Clock, AlertTriangle, AlertCircle,
  Edit, Trash, Warehouse, UserRoundCog, Boxes, ArrowLeftRight, Scale, ScrollText, Mail
} from "lucide-react";
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip as RechartsTooltip, ResponsiveContainer,
  PieChart, Pie, Cell, LineChart, Line, Legend
} from "recharts";
import "./styles.css";

// -----------------------------------------------------------------------------
// CONFIGURATION & DATA
// -----------------------------------------------------------------------------
const modules = [
  { id: "dashboard", label: "Dashboard", icon: LayoutDashboard, category: null, permissionCode: "DASHBOARD" },
  { id: "inventory", label: "Items", icon: Archive, endpoint: "/api/items", key: "itemId", category: "Master Data", permissionCode: "ITEMS" },
  { id: "warehouses", label: "Warehouses", icon: Warehouse, endpoint: "/api/warehouses", key: "warehouseId", category: "Master Data", permissionCode: "WAREHOUSES" },
  { id: "employees", label: "Employees", icon: UserRoundCog, endpoint: "/api/employees", key: "employeeId", category: "Master Data", permissionCode: "EMPLOYEES" },
  { id: "suppliers", label: "Suppliers", icon: Truck, endpoint: "/api/suppliers", key: "supplierId", category: "Master Data", permissionCode: "SUPPLIERS" },
  { id: "customers", label: "Customers", icon: Users, endpoint: "/api/customers", key: "customerId", category: "Master Data", permissionCode: "CUSTOMERS" },
  { id: "purchaseOrders", label: "Purchase Orders", icon: ClipboardList, endpoint: "/api/purchase-orders", key: "purchaseOrderId", category: "Purchasing", permissionCode: "PURCHASE_ORDERS" },
  { id: "salesOrders", label: "Sales Orders", icon: ShoppingCart, endpoint: "/api/sales-orders", key: "salesOrderId", category: "Sales", permissionCode: "SALES_ORDERS" },
  { id: "production", label: "Production Orders", icon: Factory, endpoint: "/api/production-orders", key: "productionOrderId", category: "Production", permissionCode: "PRODUCTION_ORDERS" },
  { id: "boms", label: "Bills of Materials", icon: Boxes, endpoint: "/api/boms", key: "bomId", category: "Production", permissionCode: "BOMS" },
  { id: "inventoryTransactions", label: "Stock Transactions", icon: ArrowLeftRight, endpoint: "/api/inventory-transactions", key: "transactionId", category: "Inventory", permissionCode: "INVENTORY_TRANSACTIONS" },
  { id: "warehouseStock", label: "Warehouse Stock", icon: Warehouse, endpoint: "/api/inventory-transactions/stock-by-warehouse", category: "Inventory", permissionCode: "WAREHOUSE_STOCK" },
  { id: "payments", label: "Payments", icon: WalletCards, category: "Finance", permissionCode: "PAYMENTS" },
  { id: "generalLedger", label: "General Ledger", icon: Scale, endpoint: "/api/general-ledger", key: "ledgerEntryId", category: "Finance", permissionCode: "GENERAL_LEDGER" },
  { id: "reports", label: "Reports", icon: FileBarChart, category: "Finance", permissionCode: "REPORTS" },
  { id: "notifications", label: "Notifications", icon: Mail, endpoint: "/api/notifications", key: "notificationId", category: "Administration", permissionCode: "NOTIFICATIONS" },
  { id: "auditLogs", label: "Audit Logs", icon: ScrollText, endpoint: "/api/audit-logs", key: "logId", category: "Administration", permissionCode: "AUDIT_LOGS" },
  { id: "users", label: "Users & Access", icon: UserCircle, endpoint: "/api/users", key: "userId", category: "Administration", permissionCode: "USERS" }
];

const dashboardEndpoints = {
  lowStockItems: "/api/dashboard/low-stock-items",
  productionSummary: "/api/dashboard/production-summary",
  monthlySalesSummary: "/api/dashboard/monthly-sales-summary",
  topSellingProducts: "/api/dashboard/top-selling-products",
  supplierPurchaseSummary: "/api/dashboard/supplier-purchase-summary"
};

const samples = {
  dashboard: {
    kpis: { revenue: 425000, salesOrders: 142, purchaseOrders: 56, productionOrders: 24, inventoryValue: 1250000, outstandingPayments: 35000 },
    lowStockItems: [
      { itemId: 1, itemName: "Steel Sheet (2mm)", currentStock: 18, reorderLevel: 25, itemStatus: "Low Stock" },
      { itemId: 2, itemName: "Packing Film", currentStock: 9, reorderLevel: 20, itemStatus: "Low Stock" }
    ],
    productionSummary: { totalOrders: 24, completedOrders: 16, plannedOrders: 5, inProgress: 3 },
    inventoryCapacity: [
      { name: "Warehouse A", used: 75, free: 25 },
      { name: "Warehouse B", used: 45, free: 55 },
      { name: "Cold Storage", used: 90, free: 10 }
    ],
    productionTrend: [
      { day: "Mon", planned: 20, completed: 18 },
      { day: "Tue", planned: 25, completed: 25 },
      { day: "Wed", planned: 22, completed: 20 },
      { day: "Thu", planned: 30, completed: 15 },
      { day: "Fri", planned: 28, completed: 0 }
    ],
    activityFeed: [
      { id: 1, type: "approved", message: "Purchase Order #1004 approved by Admin", time: "10 mins ago", icon: CheckCircle2, color: "var(--success)" },
      { id: 2, type: "completed", message: "Production Order #301 completed", time: "1 hour ago", icon: Factory, color: "var(--primary)" },
      { id: 3, type: "alert", message: "Stock level critical for Packing Film", time: "2 hours ago", icon: AlertTriangle, color: "var(--warning)" },
      { id: 4, type: "delivered", message: "Sales Order #801 dispatched", time: "3 hours ago", icon: Truck, color: "var(--primary)" }
    ]
  },
  inventory: [
    { itemId: 1, itemName: "Steel Sheet", itemType: "RawMaterial", currentStock: 18, reorderLevel: 25, itemStatus: "Low Stock" },
    { itemId: 8, itemName: "Finished Gear Unit", itemType: "FinishedProduct", currentStock: 142, reorderLevel: 40, itemStatus: "Active" }
  ],
  warehouses: [],
  employees: [],
  boms: [],
  inventoryTransactions: [],
  warehouseStock: [],
  generalLedger: [],
  notifications: [],
  auditLogs: [],
  suppliers: [
    { supplierId: 4, supplierName: "Metro Industrial Supply", email: "supply@example.com", supplierStatus: "Approved" }
  ],
  customers: [
    { customerId: 12, customerName: "Northwind Components", email: "orders@example.com", customerType: "Wholesale" }
  ],
  purchaseOrders: [
    { purchaseOrderId: 1004, supplierId: 4, orderDate: "2026-06-16", status: "PendingApproval", totalAmount: 182500 }
  ],
  salesOrders: [
    { salesOrderId: 801, customerId: 12, orderDate: "2026-06-15", orderStatus: "Pending", totalAmount: 95000 }
  ],
  production: [
    { productionOrderId: 301, finishedProductId: 8, quantityToProduce: 250, quantityProduced: 100, status: "In Progress", priority: "High" }
  ],
  payments: [
    { paymentId: 77, salesOrderId: 801, paymentDate: "2026-06-15", amount: 35000, paymentMethod: "Bank Transfer", paymentStatus: "Pending" }
  ],
  users: []
};

const pageConfig = {
  inventory: {
    title: "Items Master", subtitle: "Manage raw materials, finished goods, and stock parameters.",
    columns: ["itemId", "itemName", "itemType", "currentStock", "reorderLevel", "itemStatus"], statusField: "itemStatus",
    createLabel: "New Item",
    formSections: {
      "General Information": [["itemName", "Item name"], ["itemType", "Item type", "select", ["RawMaterial", "FinishedProduct"]], ["unitOfMeasure", "Unit"]],
      "Operational Parameters": [["currentStock", "Current stock", "number"], ["reorderLevel", "Reorder level", "number"], ["itemStatus", "Status", "select", ["Active", "Low Stock", "Inactive"]]]
    }
  },
  warehouses: {
    title: "Warehouses", subtitle: "Storage locations, capacity, and responsible managers.",
    columns: ["warehouseId", "warehouseName", "location", "capacity", "managerName"], key: "warehouseId", createLabel: "New Warehouse",
    formSections: { "Warehouse Details": [["warehouseName", "Warehouse name"], ["location", "Location"], ["capacity", "Capacity", "number"], ["managerName", "Manager name"]] }
  },
  employees: {
    title: "Employees", subtitle: "Employee records used throughout ERP operations.",
    columns: ["employeeId", "employeeName", "email", "contactNo", "hireDate", "salary", "employeeType"], key: "employeeId", createLabel: "New Employee",
    formSections: { "Employee Details": [["employeeName", "Employee name"], ["employeeType", "Employee type"], ["email", "Email", "email"], ["contactNo", "Contact number"], ["hireDate", "Hire date", "datetime-local"], ["salary", "Salary", "number"]] }
  },
  suppliers: {
    title: "Suppliers", subtitle: "Supplier master data and contact information.",
    columns: ["supplierId", "supplierName", "contactPerson", "email", "phone", "supplierStatus"], statusField: "supplierStatus",
    createLabel: "New Supplier",
    formSections: {
      "General Information": [["supplierName", "Supplier name"], ["supplierStatus", "Status", "select", ["Approved", "Pending", "Blocked"]]],
      "Contact Details": [["contactPerson", "Contact person"], ["email", "Email", "email"], ["phone", "Phone"]]
    }
  },
  customers: {
    title: "Customers", subtitle: "Customer accounts and sales contact details.",
    columns: ["customerId", "customerName", "customerType", "email", "contactNo"],
    createLabel: "New Customer",
    formSections: { "General Information": [["customerName", "Customer name"], ["customerType", "Type", "select", ["Retail", "Wholesale", "Corporate"]], ["email", "Email"], ["contactNo", "Contact number"]] }
  },
  purchaseOrders: {
    title: "Purchase Orders", subtitle: "Purchase planning, approval, and receiving workflow.",
    columns: ["purchaseOrderId", "supplierId", "orderDate", "status", "totalAmount"], statusField: "status",
    createLabel: "New Purchase Order",
    formSections: {
      "General Information": [["supplierId", "Supplier ID", "number"], ["employeeId", "Employee ID", "number"]],
      "Order Details": [["orderDate", "Order date", "date"], ["expectedDate", "Expected date", "date"], ["status", "Status", "select", ["Pending", "PendingApproval", "Approved", "Received", "Cancelled"]]]
    }
  },
  salesOrders: {
    title: "Sales Orders", subtitle: "Sales pipeline, delivery status, and invoice export.",
    columns: ["salesOrderId", "customerId", "orderDate", "orderStatus", "totalAmount"], statusField: "orderStatus",
    createLabel: "New Sales Order",
    formSections: { "General Information": [["customerId", "Customer ID", "number"], ["employeeId", "Employee ID", "number"], ["orderDate", "Order date", "date"], ["orderStatus", "Status", "select", ["Pending", "Delivered", "Cancelled"]]] }
  },
  production: {
    title: "Production Orders", subtitle: "Manufacturing orders, material consumption, and completion status.",
    columns: ["productionOrderId", "finishedProductId", "quantityToProduce", "quantityProduced", "status", "priority"], statusField: "status",
    createLabel: "New Production Order",
    formSections: { "Production Details": [["finishedProductId", "Finished product ID", "number"], ["employeeId", "Employee ID", "number"], ["quantityToProduce", "Quantity", "number"], ["status", "Status", "select", ["Planned", "InProgress", "Completed", "Cancelled"]], ["priority", "Priority", "select", ["Low", "Normal", "High"]]] }
  },
  boms: {
    title: "Bills of Materials", subtitle: "Define raw material requirements for finished products.",
    columns: ["bomId", "finishedProductName", "rawMaterialName", "requiredQuantity", "wastagePercentage"], key: "bomId", createLabel: "New BOM Line",
    formSections: { "Material Requirement": [["finishedProductId", "Finished product ID", "number"], ["rawMaterialId", "Raw material ID", "number"], ["requiredQuantity", "Required quantity", "number"], ["wastagePercentage", "Wastage percentage", "number"]] }
  },
  inventoryTransactions: {
    title: "Stock Transactions", subtitle: "Record stock movements and trace their warehouse origin.",
    columns: ["transactionId", "itemName", "warehouseName", "employeeName", "transactionType", "quantity", "transactionDate", "remarks"], key: "transactionId", statusField: "transactionType", createLabel: "New Transaction", allowEdit: false, allowDelete: false,
    formSections: { "Movement Details": [["itemId", "Item ID", "number"], ["warehouseId", "Warehouse ID", "number"], ["employeeId", "Employee ID", "number"], ["transactionType", "Transaction type", "select", ["StockIn", "StockOut"]], ["quantity", "Quantity", "number"], ["transactionDate", "Transaction date", "datetime-local"], ["remarks", "Remarks", "textarea"]] }
  },
  warehouseStock: {
    title: "Warehouse Stock", subtitle: "Current item balances grouped by warehouse.",
    columns: ["warehouseId", "warehouseName", "itemId", "itemName", "quantityOnHand"], readOnly: true
  },
  generalLedger: {
    title: "General Ledger", subtitle: "Accounting entries generated by ERP activity and manual journals.",
    columns: ["ledgerEntryId", "entryDate", "accountCode", "accountName", "debitAmount", "creditAmount", "sourceTable", "sourceId", "description"], key: "ledgerEntryId", createLabel: "New Journal Entry", allowEdit: false, allowDelete: false,
    formSections: { "Journal Details": [["accountCode", "Account code"], ["accountName", "Account name"], ["debitAmount", "Debit amount", "number"], ["creditAmount", "Credit amount", "number"], ["entryDate", "Entry date", "datetime-local"], ["sourceTable", "Source table"], ["sourceId", "Source ID", "number"], ["description", "Description", "textarea"]] }
  },
  notifications: {
    title: "Notifications", subtitle: "Queue and inspect operational email notifications.",
    columns: ["notificationId", "recipientEmail", "subject", "status", "sourceType", "sourceId", "createdDate", "sentDate"], key: "notificationId", statusField: "status", createLabel: "Queue Notification", allowEdit: false, allowDelete: false,
    formSections: { "Notification Details": [["recipientEmail", "Recipient email", "email"], ["subject", "Subject"], ["message", "Message", "textarea"], ["sourceType", "Source type"], ["sourceId", "Source ID", "number"]] }
  },
  auditLogs: {
    title: "Audit Logs", subtitle: "Read-only history of changes across ERP records.",
    columns: ["logId", "actionDate", "employeeName", "tableName", "actionType", "recordId", "description"], statusField: "actionType", readOnly: true
  },
  payments: { title: "Payments", subtitle: "Payment activity from sales orders.", columns: ["paymentId", "salesOrderId", "paymentDate", "amount", "paymentMethod", "paymentStatus"], statusField: "paymentStatus" },
};

// -----------------------------------------------------------------------------
// UTILITIES
// -----------------------------------------------------------------------------
function getToken() { return localStorage.getItem("erpToken") || ""; }
async function api(path, options = {}) {
  const headers = { "Content-Type": "application/json", ...(options.headers || {}) };
  const token = getToken();
  if (token) headers.Authorization = `Bearer ${token}`;
  const response = await fetch(path, { ...options, headers });
  if (!response.ok) {
    const detail = await response.text().catch(() => "");
    throw new Error(`${response.status} ${response.statusText}${detail ? `: ${detail}` : ""}`);
  }
  if (response.status === 204) return null;
  const contentType = response.headers.get("content-type") || "";
  return contentType.includes("json") ? response.json() : response.text();
}
function pageContent(payload) {
  return Array.isArray(payload) ? payload : payload?.content || [];
}
function orderByCreatedDate(rows) {
  return [...rows].sort((a, b) => new Date(b.createdDate || 0) - new Date(a.createdDate || 0));
}
function buildDashboard(raw) {
  const monthlySales = raw.monthlySalesSummary || [];
  const supplierPurchases = raw.supplierPurchaseSummary || [];
  const productionSummary = raw.productionSummary || {};
  const revenue = monthlySales.reduce((sum, row) => sum + Number(row.totalSalesAmount || 0), 0);
  const purchaseTotal = supplierPurchases.reduce((sum, row) => sum + Number(row.totalPurchaseAmount || 0), 0);

  return {
    ...samples.dashboard,
    kpis: {
      ...samples.dashboard.kpis,
      revenue,
      purchaseOrders: supplierPurchases.length,
      productionOrders: productionSummary.totalOrders || 0,
      inventoryValue: purchaseTotal
    },
    lowStockItems: raw.lowStockItems || [],
    productionSummary: {
      totalOrders: productionSummary.totalOrders || 0,
      completedOrders: productionSummary.completedOrders || 0,
      plannedOrders: productionSummary.plannedOrders || 0,
      inProgress: Math.max(
        Number(productionSummary.totalOrders || 0)
          - Number(productionSummary.completedOrders || 0)
          - Number(productionSummary.plannedOrders || 0),
        0
      )
    },
    productionTrend: monthlySales.map(row => ({
      day: row.month,
      planned: Number(row.totalSalesAmount || 0),
      completed: Number(row.totalSalesAmount || 0)
    })),
    activityFeed: (raw.notifications || []).slice(0, 5).map(notification => ({
      id: notification.notificationId,
      message: notification.subject,
      time: notification.status,
      icon: notification.status === "Sent" ? CheckCircle2 : Clock,
      color: notification.status === "Failed" ? "var(--danger)" : "var(--primary)"
    }))
  };
}
function normalizeStatus(value) {
  if (value === true) return "Enabled";
  if (value === false) return "Disabled";
  return String(value || "Open").replace(/([a-z])([A-Z])/g, "$1 $2");
}
function labelize(key) { return String(key).replace(/([a-z])([A-Z])/g, "$1 $2").replace(/^./, c => c.toUpperCase()); }
function formatValue(value) {
  if (value == null) return "-";
  if (typeof value === "number" && value > 999) return value.toLocaleString();
  if (String(value).includes("T")) return new Date(value).toLocaleDateString();
  return String(value);
}
function money(value) { return Number(value || 0).toLocaleString(undefined, { style: "currency", currency: "USD", maximumFractionDigits: 0 }); }
function downloadCSV(filename, rows, columns) {
  const csv = [columns.join(","), ...rows.map(row => columns.map(col => {
    const text = String(row[col] ?? "");
    return text.includes(",") ? `"${text.replaceAll('"', '""')}"` : text;
  }).join(","))].join("\n");
  const blob = new Blob([csv], { type: "text/csv;charset=utf-8" });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = filename;
  link.click();
  URL.revokeObjectURL(url);
}
async function downloadApiFile(path, fallbackName) {
  const headers = {};
  const token = getToken();
  if (token) headers.Authorization = `Bearer ${token}`;
  const response = await fetch(path, { headers });
  if (!response.ok) throw new Error(`${response.status} ${response.statusText}`);
  const disposition = response.headers.get("content-disposition") || "";
  const filename = disposition.match(/filename="?([^";]+)"?/i)?.[1] || fallbackName;
  const url = URL.createObjectURL(await response.blob());
  const link = document.createElement("a");
  link.href = url;
  link.download = filename;
  link.click();
  URL.revokeObjectURL(url);
}

// -----------------------------------------------------------------------------
// COMPONENTS
// -----------------------------------------------------------------------------
function Badge({ value }) {
  const text = normalizeStatus(value);
  const low = text.toLowerCase();
  let tone = "neutral";
  if (low.includes("cancel") || low.includes("critical") || low.includes("block")) tone = "danger";
  else if (low.includes("low") || low.includes("pending") || low.includes("progress")) tone = "warning";
  else if (low.includes("complete") || low.includes("approve") || low.includes("enable") || low.includes("active")) tone = "success";
  else if (low.includes("plan")) tone = "primary";
  return <span className={`badge ${tone}`}>{text}</span>;
}

function Sidebar({ active, setActive, permissions }) {
  const visibleModules = modules.filter(m => permissions.includes(`${m.permissionCode}:VIEW`));
  const categories = [...new Set(visibleModules.filter(m => m.category).map(m => m.category))];
  const hasDashboard = visibleModules.some(m => m.id === "dashboard");

  return (
    <aside className="sidebar">
      <div className="brand"><Building2 size={24} /><span>Manufacturing ERP</span></div>
      <div className="sidebar-nav">
        {hasDashboard && (
          <div className="nav-group">
            <button className={`nav-item ${active === "dashboard" ? "active" : ""}`} onClick={() => setActive("dashboard")}>
              <LayoutDashboard size={18} /><span>Dashboard</span>
            </button>
          </div>
        )}
        {categories.map(cat => (
          <div className="nav-group" key={cat}>
            <div className="nav-group-title">{cat}</div>
            {visibleModules.filter(m => m.category === cat).map(item => {
              const Icon = item.icon;
              return (
                <button key={item.id} className={`nav-item ${active === item.id ? "active" : ""}`} onClick={() => setActive(item.id)}>
                  <Icon size={18} /><span>{item.label}</span>
                </button>
              );
            })}
          </div>
        ))}
      </div>
    </aside>
  );
}

function Topbar({
  user,
  role,
  openLogin,
  moduleName,
  notifications,
  notificationsOpen,
  setNotificationsOpen,
  markNotificationSent,
  permissions
}) {
  const queuedCount = notifications.filter(notification => notification.status === "Queued").length;
  return (
    <header className="topbar">
      <div className="topbar-left">
        <span className="module-name">{moduleName}</span>
      </div>
      <div className="topbar-center">
        <div className="global-search">
          <Search size={16} />
          <input placeholder="Search everywhere (Alt+Q)" />
        </div>
      </div>
      <div className="topbar-right">
        <button className="icon-btn" title="Tasks"><Clock size={18} /></button>
        <div className="notification-wrap">
          <button
            className="icon-btn notification-button"
            title="Notifications"
            onClick={() => setNotificationsOpen(!notificationsOpen)}
          >
            <Bell size={18} />
            {queuedCount > 0 && <span className="notification-count">{queuedCount}</span>}
          </button>
          {notificationsOpen && (
            <div className="notification-menu">
              <div className="notification-menu-header">
                <span>Notifications</span>
                <span>{queuedCount} queued</span>
              </div>
              <div className="notification-list">
                {notifications.slice(0, 8).map(notification => (
                  <div className="notification-item" key={notification.notificationId}>
                    <div className="notification-main">
                      <span className="notification-subject">{notification.subject}</span>
                      <span className="notification-message">{notification.message}</span>
                      <span className="notification-meta">{formatValue(notification.createdDate)} · {notification.status}</span>
                    </div>
                    {notification.status === "Queued" && permissions.includes("NOTIFICATIONS:EDIT") && (
                      <button className="secondary mini" onClick={() => markNotificationSent(notification.notificationId)}>
                        Sent
                      </button>
                    )}
                  </div>
                ))}
                {notifications.length === 0 && <div className="empty-state">No notifications.</div>}
              </div>
            </div>
          )}
        </div>
        <button className="icon-btn" title="Settings"><Settings size={18} /></button>
        <button className="user-profile" onClick={openLogin}>
          <UserCircle size={28} color="var(--primary)" />
          <div className="user-info">
            <span className="user-name">{user}</span>
            <span className="user-role">{role}</span>
          </div>
        </button>
      </div>
    </header>
  );
}

function CustomTooltip({ active, payload, label }) {
  if (active && payload && payload.length) {
    return (
      <div className="custom-tooltip">
        <p className="label">{label}</p>
        {payload.map((entry, index) => (
          <div key={index} style={{ color: entry.color }}>
            {entry.name}: {entry.value}
          </div>
        ))}
      </div>
    );
  }
  return null;
}

function Dashboard({ data, refresh }) {
  const kpis = data.kpis || {};
  const cards = [
    { label: "Revenue (MTD)", value: money(kpis.revenue), color: "var(--primary)" },
    { label: "Sales Orders", value: kpis.salesOrders, color: "var(--success)" },
    { label: "Purchase Orders", value: kpis.purchaseOrders, color: "var(--warning)" },
    { label: "Production Orders", value: kpis.productionOrders, color: "var(--secondary)" },
    { label: "Inventory Value", value: money(kpis.inventoryValue), color: "var(--primary)" },
    { label: "Outstanding Pymt", value: money(kpis.outstandingPayments), color: "var(--danger)" }
  ];

  const COLORS = ['var(--primary)', 'var(--neutral-bg)', 'var(--danger)'];
  const pieData = [
    { name: 'Completed', value: data.productionSummary?.completedOrders || 0 },
    { name: 'Planned', value: data.productionSummary?.plannedOrders || 0 },
    { name: 'In Progress', value: data.productionSummary?.inProgress || 0 },
  ];

  return (
    <div className="page-content">
      <div className="page-header">
        <div>
          <h1 className="page-title">Executive Dashboard</h1>
          <p className="page-subtitle">Real-time overview of manufacturing operations.</p>
        </div>
        <div className="command-bar">
          <button onClick={refresh}><RefreshCcw size={14} /> Refresh Data</button>
        </div>
      </div>

      <div className="kpi-grid">
        {cards.map(card => (
          <div className="kpi-card" key={card.label}>
            <div className="kpi-header">
              <div className="kpi-indicator" style={{ backgroundColor: card.color }}></div>
              {card.label}
            </div>
            <div className="kpi-value">{card.value || 0}</div>
          </div>
        ))}
      </div>

      <div className="dashboard-cols">
        <div className="panel" style={{ gridRow: "span 2" }}>
          <div className="panel-header">
            <span className="panel-title">Production Trend (Weekly)</span>
          </div>
          <div style={{ height: 300 }}>
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={data.productionTrend || []} margin={{ top: 5, right: 20, bottom: 5, left: 0 }}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="var(--border)" />
                <XAxis dataKey="day" axisLine={false} tickLine={false} />
                <YAxis axisLine={false} tickLine={false} />
                <RechartsTooltip content={<CustomTooltip />} />
                <Legend iconType="circle" />
                <Line type="monotone" dataKey="planned" stroke="var(--text-muted)" strokeWidth={2} dot={false} />
                <Line type="monotone" dataKey="completed" stroke="var(--primary)" strokeWidth={2} />
              </LineChart>
            </ResponsiveContainer>
          </div>
          
          <div className="panel-header" style={{ marginTop: "var(--space-4)" }}>
            <span className="panel-title">Warehouse Utilization</span>
          </div>
          <div style={{ height: 250 }}>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={data.inventoryCapacity || []} layout="vertical" margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" horizontal={false} stroke="var(--border)" />
                <XAxis type="number" hide />
                <YAxis dataKey="name" type="category" axisLine={false} tickLine={false} width={100} />
                <RechartsTooltip content={<CustomTooltip />} />
                <Legend iconType="circle" />
                <Bar dataKey="used" stackId="a" fill="var(--primary)" radius={[0, 0, 0, 0]} barSize={20} />
                <Bar dataKey="free" stackId="a" fill="var(--neutral-bg)" radius={[0, 4, 4, 0]} barSize={20} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="panel">
          <div className="panel-header">
            <span className="panel-title">Production Status</span>
          </div>
          <div style={{ height: 220 }}>
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie data={pieData} cx="50%" cy="50%" innerRadius={60} outerRadius={80} fill="#8884d8" paddingAngle={5} dataKey="value">
                  {pieData.map((entry, index) => <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />)}
                </Pie>
                <RechartsTooltip content={<CustomTooltip />} />
                <Legend iconType="circle" layout="vertical" verticalAlign="middle" align="right" />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="panel">
          <div className="panel-header">
            <span className="panel-title">Recent Activity</span>
          </div>
          <div className="feed-list">
            {(data.activityFeed || []).map(feed => {
              const Icon = feed.icon;
              return (
                <div className="feed-item" key={feed.id}>
                  <div className="feed-icon"><Icon size={16} color={feed.color} /></div>
                  <div className="feed-content">
                    <span className="feed-text">{feed.message}</span>
                    <span className="feed-time">{feed.time}</span>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </div>
    </div>
  );
}

function ModalForm({ title, sections, onClose, onSave, initialValues = {} }) {
  const [values, setValues] = useState(initialValues);
  return (
    <div className="modal-overlay">
      <div className="modal-sidepanel">
        <div className="modal-header">
          <span className="modal-title">{title}</span>
          <button className="icon-btn" onClick={onClose}><X size={20} /></button>
        </div>
        <div className="modal-body">
          <form id="side-form" onSubmit={e => { e.preventDefault(); onSave(values); }}>
            {Object.entries(sections).map(([sectionTitle, fields]) => (
              <div className="form-section" key={sectionTitle}>
                <div className="form-section-title">{sectionTitle}</div>
                <div className="form-grid">
                  {fields.map(([name, label, type = "text", options]) => (
                    <label key={name} className={type === "textarea" ? "full" : ""}>
                      {label}
                      {type === "select" ? (
                        <select value={values[name] || ""} onChange={e => setValues({ ...values, [name]: e.target.value })} required>
                          <option value="" disabled>Select...</option>
                          {options.map(opt => <option key={opt} value={opt}>{opt}</option>)}
                        </select>
                      ) : type === "textarea" ? (
                        <textarea value={values[name] || ""} onChange={e => setValues({ ...values, [name]: e.target.value })} required />
                      ) : (
                        <input type={type} value={values[name] || ""} onChange={e => setValues({ ...values, [name]: type === "number" && e.target.value ? Number(e.target.value) : e.target.value })} required />
                      )}
                    </label>
                  ))}
                </div>
              </div>
            ))}
          </form>
        </div>
        <div className="modal-footer">
          <button className="secondary" onClick={onClose}>Cancel</button>
          <button type="submit" form="side-form">Save Record</button>
        </div>
      </div>
    </div>
  );
}

function DataPage({ config, rows, refresh, loading, error, onCreate, onUpdate, onDelete, permissions = [], permissionCode }) {
  const [editing, setEditing] = useState(false);
  const [editingRow, setEditingRow] = useState(null);

  const canCreate = !config.readOnly && config.allowCreate !== false && permissions.includes(`${permissionCode}:CREATE`);
  const canEdit = !config.readOnly && config.allowEdit !== false && permissions.includes(`${permissionCode}:EDIT`);
  const canDelete = !config.readOnly && config.allowDelete !== false && permissions.includes(`${permissionCode}:DELETE`);
  
  return (
    <div className="page-content">
      <div className="page-header">
        <div>
          <h1 className="page-title">{config.title}</h1>
          <p className="page-subtitle">{config.subtitle}</p>
        </div>
        <div className="command-bar">
          {config.createLabel && canCreate && <button className="primary" onClick={() => { setEditingRow(null); setEditing(true); }}><Plus size={14} /> {config.createLabel}</button>}
          <div className="command-divider"></div>
          <button onClick={() => downloadCSV(`${config.title}.csv`, rows, config.columns)}><Download size={14} /> Export</button>
          <button onClick={refresh}><RefreshCcw size={14} /> Refresh</button>
        </div>
      </div>

      {error && <div className="inline-error">{error}</div>}
      <div className="table-container">
        <div className="table-toolbar">
          <div className="command-bar" style={{ border: "none", background: "transparent", padding: 0 }}>
            <button><Filter size={14} /> Filter</button>
            <button><SlidersHorizontal size={14} /> Sort</button>
          </div>
          <span style={{ fontSize: 13, color: "var(--text-muted)" }}>{loading ? "Loading..." : `${rows.length} records`}</span>
        </div>
        <table>
          <thead>
            <tr>
              {config.columns.map(col => <th key={col}>{labelize(col)}</th>)}
              {(canEdit || canDelete) && <th>Actions</th>}
            </tr>
          </thead>
          <tbody>
            {rows.map((row, index) => (
              <tr key={index}>
                {config.columns.map(col => (
                  <td key={col}>
                    {col === config.statusField ? <Badge value={row[col]} /> : formatValue(row[col])}
                    {col === "completionPercentage" && (
                      <div className="progress-bar-container">
                        <div className="progress-bar-fill" style={{ width: `${row[col]}%`, background: row[col] === 100 ? "var(--success)" : "var(--primary)" }}></div>
                      </div>
                    )}
                  </td>
                ))}
                {(canEdit || canDelete) && (
                  <td className="actions-cell">
                    {canEdit && <button className="icon-btn mini" onClick={() => { setEditingRow(row); setEditing(true); }}><Edit size={14} /></button>}
                    {canDelete && <button className="icon-btn mini danger" onClick={() => { if(confirm("Are you sure you want to delete this record?")) { onDelete(row[config.key]).then(refresh); } }}><Trash size={14} /></button>}
                  </td>
                )}
              </tr>
            ))}
            {rows.length === 0 && <tr><td colSpan={config.columns.length} style={{ textAlign: "center", padding: "var(--space-4)" }}>{loading ? "Loading records..." : "No records found."}</td></tr>}
          </tbody>
        </table>
      </div>

      {editing && (
        <ModalForm title={editingRow ? `Edit ${config.title}` : config.createLabel} sections={config.formSections || {}} initialValues={editingRow || {}} onClose={() => setEditing(false)} onSave={async values => {
          const requestFields = Object.values(config.formSections || {}).flat().map(field => field[0]);
          const request = Object.fromEntries(requestFields.map(field => [field, values[field]]));
          if (editingRow) {
            await onUpdate(editingRow[config.key], request);
          } else {
            await onCreate(request);
          }
          setEditing(false);
          refresh();
        }} />
      )}
    </div>
  );
}

function UserAccessPage({ rows, refresh, loading, error, permissions }) {
  const [catalog, setCatalog] = useState({});
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(null);
  const [saveError, setSaveError] = useState("");
  const canCreate = permissions.includes("USERS:CREATE");
  const canEdit = permissions.includes("USERS:EDIT");

  useEffect(() => {
    api("/api/users/permission-catalog").then(setCatalog).catch(e => setSaveError(`Permission catalog unavailable: ${e.message}`));
  }, []);

  function openEditor(user = null) {
    const selected = {};
    (user?.modulePermissions || []).forEach(entry => { selected[entry.module] = [...(entry.actions || [])]; });
    setEditing(user);
    setForm({
      username: user?.username || "",
      password: "",
      employeeId: user?.employeeId || "",
      enabled: user ? Boolean(user.enabled) : true,
      selected
    });
    setSaveError("");
  }

  function setModule(module, enabled) {
    setForm(current => ({ ...current, selected: { ...current.selected, [module]: enabled ? ["VIEW"] : [] } }));
  }

  function setAction(module, action, checked) {
    setForm(current => {
      const actions = new Set(current.selected[module] || []);
      if (checked) actions.add(action); else actions.delete(action);
      if (action !== "VIEW" && checked) actions.add("VIEW");
      if (action === "VIEW" && !checked) actions.clear();
      return { ...current, selected: { ...current.selected, [module]: [...actions] } };
    });
  }

  async function saveUser(event) {
    event.preventDefault();
    setSaveError("");
    const modulePermissions = Object.entries(form.selected)
      .filter(([, actions]) => actions.length)
      .map(([module, actions]) => ({ module, actions }));
    const common = { username: form.username, enabled: form.enabled, employeeId: form.employeeId ? Number(form.employeeId) : null, modulePermissions };
    try {
      if (editing) {
        await api(`/api/users/${editing.userId}`, { method: "PUT", body: JSON.stringify({ ...common, newPassword: form.password || null }) });
      } else {
        await api("/api/users", { method: "POST", body: JSON.stringify({ ...common, password: form.password }) });
      }
      setForm(null);
      await refresh();
    } catch (e) {
      setSaveError(`Unable to save user: ${e.message}`);
    }
  }

  return (
    <div className="page-content">
      <div className="page-header">
        <div><h1 className="page-title">Users & Access</h1><p className="page-subtitle">Create individual users and assign access per module and action.</p></div>
        <div className="command-bar">{canCreate && <button onClick={() => openEditor()}><Plus size={14} /> New User</button>}<button onClick={refresh}><RefreshCcw size={14} /> Refresh</button></div>
      </div>
      {(error || (!form && saveError)) && <div className="inline-error">{error || saveError}</div>}
      <div className="table-container">
        <table><thead><tr><th>User ID</th><th>Username</th><th>Employee</th><th>Status</th><th>Module Access</th>{canEdit && <th>Actions</th>}</tr></thead>
          <tbody>{rows.map(user => {
            const enabledModules = user.modulePermissions?.filter(p => p.actions?.includes("VIEW")) || [];
            return <tr key={user.userId}><td>{user.userId}</td><td><strong>{user.username}</strong></td><td>{user.employeeId || "-"}</td><td><Badge value={user.enabled} /></td><td><div className="access-summary">{enabledModules.length ? enabledModules.map(p => <span key={p.module}>{p.module.replaceAll("_", " ")}</span>) : <span className="muted-access">No access</span>}</div></td>{canEdit && <td><button className="icon-btn mini" onClick={() => openEditor(user)}><Edit size={14} /></button></td>}</tr>;
          })}{!rows.length && <tr><td colSpan={6} className="empty-table">{loading ? "Loading users..." : "No users found."}</td></tr>}</tbody>
        </table>
      </div>
      {form && <div className="modal-backdrop"><div className="modal-panel access-modal">
        <div className="modal-header"><div><h2>{editing ? "Edit User Access" : "Create User"}</h2><p>{editing ? `Update ${editing.username}'s account and permissions.` : "Create an account with explicitly assigned module access."}</p></div><button className="icon-button" onClick={() => setForm(null)}><X size={18} /></button></div>
        <form id="user-access-form" className="modal-body" onSubmit={saveUser}>
          {saveError && <div className="inline-error">{saveError}</div>}
          <div className="form-section"><div className="form-section-title">Account Details</div><div className="form-grid">
            <label>Username<input value={form.username} onChange={e => setForm({ ...form, username: e.target.value })} required /></label>
            <label>{editing ? "New password (optional)" : "Password"}<input type="password" value={form.password} onChange={e => setForm({ ...form, password: e.target.value })} required={!editing} minLength={6} /></label>
            <label>Employee ID (optional)<input type="number" min="1" value={form.employeeId} onChange={e => setForm({ ...form, employeeId: e.target.value })} /></label>
            <label className="toggle-label"><input type="checkbox" checked={form.enabled} onChange={e => setForm({ ...form, enabled: e.target.checked })} /> Account enabled</label>
          </div></div>
          <div className="form-section"><div className="form-section-title">Module Permissions</div><p className="permission-help">Enable a module, then choose exactly what this user can do inside it. View is automatically required for every other action.</p>
            <div className="permission-matrix"><div className="permission-row permission-head"><span>Module</span><span>Enabled</span><span>Allowed actions</span></div>
              {Object.entries(catalog).map(([module, actions]) => {
                const selected = form.selected[module] || [];
                const enabled = selected.includes("VIEW");
                return <div className="permission-row" key={module}><strong>{module.replaceAll("_", " ")}</strong><label className="permission-enable"><input type="checkbox" checked={enabled} onChange={e => setModule(module, e.target.checked)} /><span>{enabled ? "On" : "Off"}</span></label><div className="action-checks">{actions.map(action => <label key={action} className={!enabled ? "disabled-action" : ""}><input type="checkbox" checked={selected.includes(action)} disabled={!enabled && action !== "VIEW"} onChange={e => setAction(module, action, e.target.checked)} />{action}</label>)}</div></div>;
              })}
            </div>
          </div>
        </form>
        <div className="modal-footer"><button className="secondary" onClick={() => setForm(null)}>Cancel</button><button type="submit" form="user-access-form">{editing ? "Save Changes" : "Create User"}</button></div>
      </div></div>}
    </div>
  );
}

function ReportsPage() {
  const [invoiceId, setInvoiceId] = useState("");
  const [error, setError] = useState("");
  const reports = [
    ["Monthly Sales Summary", "Sales totals grouped by month.", "/api/reports/monthly-sales-summary.csv", "monthly-sales-summary.csv"],
    ["Top Selling Products", "Product sales volume and revenue ranking.", "/api/reports/top-selling-products.csv", "top-selling-products.csv"],
    ["Supplier Purchase Summary", "Purchasing totals grouped by supplier.", "/api/reports/supplier-purchase-summary.csv", "supplier-purchase-summary.csv"]
  ];
  async function download(path, name) {
    setError("");
    try { await downloadApiFile(path, name); } catch (e) { setError(`Report download failed: ${e.message}`); }
  }
  return (
    <div className="page-content">
      <div className="page-header"><div><h1 className="page-title">Reports</h1><p className="page-subtitle">Export operational summaries and customer invoices as CSV.</p></div></div>
      {error && <div className="inline-error">{error}</div>}
      <div className="report-grid">
        {reports.map(([title, description, path, name]) => (
          <div className="report-card" key={title}><FileBarChart size={22} /><div><h3>{title}</h3><p>{description}</p></div><button onClick={() => download(path, name)}><Download size={14} /> Download CSV</button></div>
        ))}
        <div className="report-card"><FileBarChart size={22} /><div><h3>Sales Invoice</h3><p>Export the invoice for a specific sales order.</p></div><div className="report-invoice"><input type="number" min="1" placeholder="Sales order ID" value={invoiceId} onChange={e => setInvoiceId(e.target.value)} /><button disabled={!invoiceId} onClick={() => download(`/api/reports/sales-orders/${invoiceId}/invoice.csv`, `sales-invoice-${invoiceId}.csv`)}><Download size={14} /> Download</button></div></div>
      </div>
    </div>
  );
}

function LoginPage({ onLogin }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [showPw, setShowPw] = useState(false);
  return (
    <div className="login-page">
      <div className="login-bg-shapes">
        <div className="login-shape s1" />
        <div className="login-shape s2" />
        <div className="login-shape s3" />
      </div>
      <div className="login-left">
        <div className="login-brand">
          <div className="login-brand-icon"><Building2 size={32} color="#fff" /></div>
          <div>
            <div className="login-brand-name">Manufacturing ERP</div>
            <div className="login-brand-sub">Enterprise Resource Planning</div>
          </div>
        </div>
        <div className="login-hero">
          <h1 className="login-hero-title">Streamline your<br />manufacturing operations</h1>
          <p className="login-hero-desc">A unified platform for inventory, production, procurement, and sales — built for modern manufacturing teams.</p>
          <div className="login-features">
            {[
              { icon: "📦", label: "Real-time inventory tracking" },
              { icon: "🏭", label: "Production order management" },
              { icon: "📊", label: "Executive dashboards & reports" },
              { icon: "🔒", label: "Role-based access control" }
            ].map(f => (
              <div className="login-feature" key={f.label}>
                <span className="login-feature-icon">{f.icon}</span>
                <span>{f.label}</span>
              </div>
            ))}
          </div>
        </div>
      </div>
      <div className="login-right">
        <form className="login-card" onSubmit={async e => {
          e.preventDefault();
          setError("");
          setSubmitting(true);
          try {
            await onLogin(username, password);
          } catch (loginError) {
            setError(loginError.message.includes("401") ? "Invalid username or password." : `Login failed: ${loginError.message}`);
          } finally {
            setSubmitting(false);
          }
        }}>
          <div className="login-card-header">
            <h2 className="login-card-title">Welcome back</h2>
            <p className="login-card-sub">Sign in to your workspace</p>
          </div>
          {error && <div className="form-error" role="alert">{error}</div>}
          <div className="login-fields">
            <label htmlFor="login-username">
              Username
              <input
                id="login-username"
                type="text"
                autoComplete="username"
                placeholder="Enter your username"
                value={username}
                onChange={e => setUsername(e.target.value)}
                required
                autoFocus
              />
            </label>
            <label htmlFor="login-password">
              Password
              <div className="pw-wrap">
                <input
                  id="login-password"
                  type={showPw ? "text" : "password"}
                  autoComplete="current-password"
                  placeholder="Enter your password"
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                  required
                />
                <button type="button" className="pw-toggle" onClick={() => setShowPw(v => !v)} tabIndex={-1}>
                  {showPw ? "Hide" : "Show"}
                </button>
              </div>
            </label>
          </div>
          <button id="login-submit" type="submit" className="login-submit" disabled={submitting}>
            {submitting
              ? <><span className="spinner" />Signing in…</>
              : <><LogIn size={16} />Sign In</>
            }
          </button>
          <p className="login-footer-note">Access is restricted to authorized personnel only.</p>
        </form>
      </div>
    </div>
  );
}

// -----------------------------------------------------------------------------
// MAIN APP
// -----------------------------------------------------------------------------
function App() {
  const [active, setActive] = useState("dashboard");
  const [user, setUser] = useState(localStorage.getItem("erpUser") || "");
  const [role, setRole] = useState((localStorage.getItem("erpRole") || "").replace(/^ROLE_/, ""));
  const [permissions, setPermissions] = useState(JSON.parse(localStorage.getItem("erpPermissions") || "[]"));
  const [authenticated, setAuthenticated] = useState(!!getToken());
  const [data, setData] = useState(samples);
  const [notifications, setNotifications] = useState([]);
  const [notificationsOpen, setNotificationsOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const [sessionMessage, setSessionMessage] = useState("");

  const activeModule = modules.find(m => m.id === active);

  async function loadNotifications() {
    if (!getToken()) return [];
    const payload = await api("/api/notifications?sort=createdDate,desc&size=20");
    const rows = orderByCreatedDate(pageContent(payload));
    setNotifications(rows);
    return rows;
  }

  async function loadDashboard() {
    if (!getToken()) return;
    setLoading(true);
    try {
      const [
        lowStockItems,
        productionSummary,
        monthlySalesSummary,
        topSellingProducts,
        supplierPurchaseSummary,
        notificationRows
      ] = await Promise.all([
        api(dashboardEndpoints.lowStockItems),
        api(dashboardEndpoints.productionSummary),
        api(dashboardEndpoints.monthlySalesSummary),
        api(dashboardEndpoints.topSellingProducts),
        api(dashboardEndpoints.supplierPurchaseSummary),
        loadNotifications().catch(() => [])
      ]);
      setData(current => ({
        ...current,
        dashboard: buildDashboard({
          lowStockItems,
          productionSummary,
          monthlySalesSummary,
          topSellingProducts,
          supplierPurchaseSummary,
          notifications: notificationRows
        })
      }));
      setErrors(current => ({ ...current, dashboard: "" }));
    } catch (error) {
      setErrors(current => ({ ...current, dashboard: `Dashboard API unavailable: ${error.message}` }));
    } finally {
      setLoading(false);
    }
  }

  async function loadModule(moduleId = active) {
    if (moduleId === "dashboard") { await loadDashboard(); return; }
    const module = modules.find(item => item.id === moduleId);
    if (!module?.endpoint || !getToken()) return;
    setLoading(true);
    try {
      const payload = await api(`${module.endpoint}?size=100`);
      setData(current => ({ ...current, [moduleId]: pageContent(payload) }));
      setErrors(current => ({ ...current, [moduleId]: "" }));
    } catch (error) {
      setErrors(current => ({ ...current, [moduleId]: `Backend API unavailable: ${error.message}. Showing sample data.` }));
    } finally {
      setLoading(false);
    }
  }

  async function createRecord(values) {
    const module = modules.find(item => item.id === active);
    if (!module?.endpoint) return;
    try {
      await api(module.endpoint, { method: "POST", body: JSON.stringify(values) });
      setErrors(current => ({ ...current, [active]: "" }));
    } catch (error) {
      setErrors(current => ({ ...current, [active]: `Save failed: ${error.message}` }));
      throw error;
    }
  }

  async function updateRecord(id, values) {
    const module = modules.find(item => item.id === active);
    if (!module?.endpoint) return;
    try {
      await api(`${module.endpoint}/${id}`, { method: "PUT", body: JSON.stringify(values) });
      setErrors(current => ({ ...current, [active]: "" }));
    } catch (error) {
      setErrors(current => ({ ...current, [active]: `Update failed: ${error.message}` }));
      throw error;
    }
  }

  async function deleteRecord(id) {
    const module = modules.find(item => item.id === active);
    if (!module?.endpoint) return;
    try {
      await api(`${module.endpoint}/${id}`, { method: "DELETE" });
      setErrors(current => ({ ...current, [active]: "" }));
    } catch (error) {
      setErrors(current => ({ ...current, [active]: `Delete failed: ${error.message}` }));
      throw error;
    }
  }

  async function markNotificationSent(notificationId) {
    await api(`/api/notifications/${notificationId}/sent`, { method: "POST" });
    await loadNotifications();
    if (active === "dashboard") await loadDashboard();
  }

  function logout() {
    localStorage.removeItem("erpToken");
    localStorage.removeItem("erpUser");
    localStorage.removeItem("erpRole");
    localStorage.removeItem("erpPermissions");
    setUser("");
    setRole("");
    setPermissions([]);
    setAuthenticated(false);
    setData(samples);
    setNotifications([]);
  }

  function resetStaleSession() {
    localStorage.removeItem("erpToken");
    localStorage.removeItem("erpUser");
    localStorage.removeItem("erpRole");
    localStorage.removeItem("erpPermissions");
    setUser("");
    setRole("");
    setPermissions([]);
    setAuthenticated(false);
    setData(samples);
    setNotifications([]);
    setSessionMessage("Access permissions were upgraded. Please sign in again.");
  }

  async function login(username, password) {
    const response = await api("/api/auth/login", {
      method: "POST",
      body: JSON.stringify({ username, password })
    });
    const safeRole = (response.role || "").replace(/^ROLE_/, "");
    const perms = response.permissions || [];
    if (!perms.some(permission => permission.endsWith(":VIEW"))) {
      throw new Error("This account has no module access. Ask a user administrator to assign module permissions.");
    }
    localStorage.setItem("erpToken", response.token);
    localStorage.setItem("erpUser", response.username);
    localStorage.setItem("erpRole", safeRole);
    localStorage.setItem("erpPermissions", JSON.stringify(perms));
    setUser(response.username);
    setRole(safeRole);
    setPermissions(perms);
    setAuthenticated(true);
    const firstAllowedModule = modules.find(module => perms.includes(`${module.permissionCode}:VIEW`))?.id;
    if (firstAllowedModule) setActive(firstAllowedModule);
    setTimeout(() => {
      if (firstAllowedModule) loadModule(firstAllowedModule);
      if (perms.includes("NOTIFICATIONS:VIEW")) loadNotifications().catch(() => []);
    }, 0);
  }

  useEffect(() => {
    if (authenticated) {
      const allowed = modules.some(module => module.id === active && permissions.includes(`${module.permissionCode}:VIEW`));
      if (!allowed) {
        const firstAllowed = modules.find(module => permissions.includes(`${module.permissionCode}:VIEW`));
        if (firstAllowed) setActive(firstAllowed.id);
        else resetStaleSession();
        return;
      }
      loadModule(active);
      if (permissions.includes("NOTIFICATIONS:VIEW")) loadNotifications().catch(() => []);
    }
  }, [active, authenticated, permissions]);

  if (!authenticated) {
    return <><LoginPage onLogin={async (username, password) => { await login(username, password); setSessionMessage(""); }} />{sessionMessage && <div className="session-message">{sessionMessage}</div>}</>;
  }

  return (
    <div className="app-shell">
      <Sidebar active={active} setActive={setActive} permissions={permissions} />
      <div className="workspace">
        <Topbar
          user={user}
          role="Custom access"
          openLogin={logout}
          moduleName={activeModule?.label}
          notifications={notifications}
          notificationsOpen={notificationsOpen}
          setNotificationsOpen={setNotificationsOpen}
          markNotificationSent={markNotificationSent}
          permissions={permissions}
        />
        <main className="main-content">
          {active === "dashboard" && (
            <>
              {errors.dashboard && <div className="inline-error">{errors.dashboard}</div>}
              <Dashboard data={data.dashboard} refresh={() => loadDashboard()} />
            </>
          )}
          {active === "reports" && <ReportsPage />}
          {active === "users" && <UserAccessPage rows={data.users || []} refresh={() => loadModule("users")} loading={loading} error={errors.users} permissions={permissions} />}
          {pageConfig[active] && active !== "users" && (
            <DataPage
              config={{ ...pageConfig[active], key: activeModule?.key }}
              rows={data[active] || []}
              refresh={() => loadModule(active)}
              loading={loading}
              error={errors[active]}
              onCreate={createRecord}
              onUpdate={updateRecord}
              onDelete={deleteRecord}
              permissions={permissions}
              permissionCode={activeModule?.permissionCode}
            />
          )}
        </main>
      </div>
    </div>
  );
}

createRoot(document.getElementById("root")).render(<App />);

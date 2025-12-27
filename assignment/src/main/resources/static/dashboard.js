const API_URL = 'http://localhost:8080';
let orders = [];
let customers = [];
let products = [];
let contacts = [];

function getToken() {
    return localStorage.getItem('token');
}

function getHeaders() {
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
    };
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    window.location.href = 'index.html';
}

function checkAuth() {
    if (!getToken()) {
        window.location.href = 'index.html';
        return;
    }
    document.getElementById('username').textContent = localStorage.getItem('username');
}

async function loadOrders() {
    const ordersList = document.getElementById('ordersList');
    ordersList.innerHTML = '<div class="empty-state"><h3>Loading orders...</h3></div>';

    try {
        const response = await fetch(`${API_URL}/orders`, {
            headers: getHeaders()
        });

        if (response.status === 401) {
            logout();
            return;
        }

        if (response.ok) {
            orders = await response.json();
            displayOrders(orders);
        } else {
            ordersList.innerHTML = '<div class="empty-state"><h3>Error loading orders</h3></div>';
        }
    } catch (error) {
        ordersList.innerHTML = '<div class="empty-state"><h3>Connection error</h3></div>';
    }
}

function displayOrders(orders) {
    const ordersList = document.getElementById('ordersList');

    if (orders.length === 0) {
        ordersList.innerHTML = '<div class="empty-state"><h3>No orders found</h3><p>Create your first order to get started</p></div>';
        return;
    }

    ordersList.innerHTML = orders.map(order => `
        <div class="order-card">
            <div class="order-header">
                <div class="order-id">Order #${order.orderId}</div>
                <div class="order-date">${order.orderDate}</div>
            </div>
            <div class="order-info">
                <div><strong>Customer:</strong> ${order.customerName}</div>
                <div><strong>Items:</strong> ${order.items.length}</div>
                <div><strong>Total Qty:</strong> ${order.items.reduce((sum, item) => sum + item.quantity, 0)}</div>
            </div>
            <div class="order-actions">
                <button class="btn-small btn-view" onclick="viewOrder(${order.orderId})">View Details</button>
                <button class="btn-small btn-delete" onclick="deleteOrder(${order.orderId})">Delete</button>
            </div>
        </div>
    `).join('');
}

function switchView(viewName) {
    document.querySelectorAll('.section').forEach(el => el.style.display = 'none');
    document.querySelectorAll('.tab').forEach(el => el.classList.remove('active'));

    document.getElementById(`${viewName}Section`).style.display = 'block';

    const buttons = document.querySelectorAll('.tab');
    buttons.forEach(btn => {
        if (btn.innerText.toLowerCase() === viewName) {
            btn.classList.add('active');
        }
    });

    if (viewName === 'customers') displayCustomers();
    if (viewName === 'products') displayProducts();
}

function displayCustomers() {
    const container = document.getElementById('customersList');
    if (customers.length === 0) {
        container.innerHTML = '<p>No customers found.</p>';
        return;
    }
    container.innerHTML = `
        <table style="width: 100%; border-collapse: collapse;">
            <thead>
                <tr style="text-align: left; background: rgba(0,0,0,0.05);">
                    <th style="padding: 12px;">ID</th>
                    <th style="padding: 12px;">Name</th>
                </tr>
            </thead>
            <tbody>
                ${customers.map(c => `
                    <tr style="border-bottom: 1px solid rgba(0,0,0,0.05);">
                        <td style="padding: 12px;">${c.customerId}</td>
                        <td style="padding: 12px;">${c.firstName} ${c.lastName}</td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;
}

function displayProducts() {
    const container = document.getElementById('productsList');
    if (products.length === 0) {
        container.innerHTML = '<p>No products found.</p>';
        return;
    }
    container.innerHTML = `
        <table style="width: 100%; border-collapse: collapse;">
            <thead>
                <tr style="text-align: left; background: rgba(0,0,0,0.05);">
                    <th style="padding: 12px;">ID</th>
                    <th style="padding: 12px;">Name</th>
                    <th style="padding: 12px;">Color</th>
                    <th style="padding: 12px;">Size</th>
                </tr>
            </thead>
            <tbody>
                ${products.map(p => `
                    <tr style="border-bottom: 1px solid rgba(0,0,0,0.05);">
                        <td style="padding: 12px;">${p.productId}</td>
                        <td style="padding: 12px;">${p.productName}</td>
                        <td style="padding: 12px;">${p.color}</td>
                        <td style="padding: 12px;">${p.size}</td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;
}

async function fetchData(endpoint) {
    try {
        const response = await fetch(`${API_URL}/data/${endpoint}`, {
            headers: getHeaders()
        });
        if (response.ok) return await response.json();
    } catch (e) { console.error(`Failed to fetch ${endpoint}`, e); }
    return [];
}

async function loadFormData() {
    [customers, products, contacts] = await Promise.all([
        fetchData('customers'),
        fetchData('products'),
        fetchData('contacts')
    ]);
}

function populateSelect(selectElement, data, valueKey, labelFn) {
    selectElement.innerHTML = '<option value="">Select...</option>';
    data.forEach(item => {
        const option = document.createElement('option');
        option.value = item[valueKey];
        option.textContent = labelFn(item);
        selectElement.appendChild(option);
    });
}

function showCreateOrder() {
    document.getElementById('createOrderModal').style.display = 'block';
    document.getElementById('orderDate').valueAsDate = new Date();

    if (customers.length === 0) loadFormData().then(updateDropdowns);
    else updateDropdowns();
}

function updateDropdowns() {
    populateSelect(document.getElementById('customerId'), customers, 'customerId', c => `${c.firstName} ${c.lastName}`);
    populateSelect(document.getElementById('shippingContactId'), contacts, 'contactMechId', c => `${c.streetAddress}, ${c.city}`);
    populateSelect(document.getElementById('billingContactId'), contacts, 'contactMechId', c => `${c.streetAddress}, ${c.city}`);

    document.querySelectorAll('.productId').forEach(select => {
        if (select.children.length <= 1) {
            populateSelect(select, products, 'productId', p => `${p.productName} (${p.color}, ${p.size})`);
        }
    });
}

function addItem() {
    const container = document.getElementById('itemsContainer');
    const itemGroup = document.createElement('div');
    itemGroup.className = 'item-group';
    itemGroup.innerHTML = `
        <div class="form-group">
            <label>Product</label>
            <select class="productId" required></select>
        </div>
        <div class="form-group">
            <label>Quantity</label>
            <input type="number" class="quantity" required min="1" value="1">
        </div>
        <div class="form-group">
            <label>Status</label>
            <select class="status">
                <option>PENDING</option>
                <option>CONFIRMED</option>
                <option>SHIPPED</option>
                <option>DELIVERED</option>
            </select>
        </div>
    `;
    container.appendChild(itemGroup);

    const newSelect = itemGroup.querySelector('.productId');
    populateSelect(newSelect, products, 'productId', p => `${p.productName} (${p.color}, ${p.size})`);
}

document.getElementById('createOrderForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const errorDiv = document.getElementById('orderError');
    errorDiv.textContent = '';

    const items = [];
    document.querySelectorAll('.item-group').forEach(group => {
        items.push({
            productId: parseInt(group.querySelector('.productId').value),
            quantity: parseInt(group.querySelector('.quantity').value),
            status: group.querySelector('.status').value
        });
    });

    const orderData = {
        customerId: parseInt(document.getElementById('customerId').value),
        orderDate: document.getElementById('orderDate').value,
        shippingContactMechId: parseInt(document.getElementById('shippingContactId').value),
        billingContactMechId: parseInt(document.getElementById('billingContactId').value),
        items: items
    };

    try {
        const response = await fetch(`${API_URL}/orders`, {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify(orderData)
        });

        if (response.ok) {
            closeModal();
            loadOrders();
        } else {
            const error = await response.json();
            errorDiv.textContent = error.message || 'Failed to create order';
        }
    } catch (error) {
        errorDiv.textContent = 'Connection error. Please try again.';
    }
});

function getStatusClass(status) {
    status = status.toLowerCase();
    return `status-badge status-${status}`;
}

async function viewOrder(orderId) {
    try {
        const response = await fetch(`${API_URL}/orders/${orderId}`, {
            headers: getHeaders()
        });

        if (response.ok) {
            const order = await response.json();
            const detailsDiv = document.getElementById('orderDetails');
            detailsDiv.innerHTML = `
                <div class="detail-row">
                    <span class="detail-label">Order ID:</span>
                    <span class="detail-value">${order.orderId}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Customer:</span>
                    <span class="detail-value">${order.customerName}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Order Date:</span>
                    <span class="detail-value">${order.orderDate}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Shipping Address:</span>
                    <span class="detail-value">${order.shippingAddress}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Billing Address:</span>
                    <span class="detail-value">${order.billingAddress}</span>
                </div>
                <h3 style="margin-top: 20px; margin-bottom: 10px;">Order Items:</h3>
                ${order.items.map(item => `
                    <div class="detail-row">
                        <span class="detail-label">${item.productName} (${item.color}, ${item.size})</span>
                        <span class="detail-value">Qty: ${item.quantity} - <span class="${getStatusClass(item.status)}">${item.status}</span></span>
                    </div>
                `).join('')}
            `;
            document.getElementById('orderDetailsModal').style.display = 'block';
        }
    } catch (error) {
        alert('Error loading order details');
    }
}

function closeDetailsModal() {
    document.getElementById('orderDetailsModal').style.display = 'none';
}

async function deleteOrder(orderId) {
    if (!confirm('Are you sure you want to delete this order?')) {
        return;
    }

    try {
        const response = await fetch(`${API_URL}/orders/${orderId}`, {
            method: 'DELETE',
            headers: getHeaders()
        });

        if (response.ok) {
            loadOrders();
        } else {
            alert('Failed to delete order');
        }
    } catch (error) {
        alert('Connection error');
    }
}

function closeModal() {
    document.getElementById('createOrderModal').style.display = 'none';
    document.getElementById('createOrderForm').reset();
    document.getElementById('orderError').textContent = '';
}

checkAuth();
loadOrders();
loadFormData();

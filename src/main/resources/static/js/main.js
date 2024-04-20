const catalogDropdown = document.querySelector('#catalogDropdown');
const schemaDropdown = document.querySelector('#schemaDropdown');
const tableDropdown = document.querySelector('#tableDropdown');
const viewDropdown = document.querySelector('#viewDropdown');
const tableView = document.querySelector('.tableView');


async function getSchemaTablesMap() {
  const response = await fetch('/api/getSchemaTablesMap', {method: 'GET'});
  return await response.json();
}

async function getSchemaViewsMap() {
  const response = await fetch('/api/getSchemaViewsMap', {method: 'GET'});
  return await response.json();
}

async function updateSchema(schemaName) {
  const endpoint = '/api/setSchema';
  const response = await fetch(endpoint, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: `schemaName=${encodeURIComponent(schemaName)}`,
  });
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }
  console.log('Schema updated successfully');
}

async function updateCatalog(catalogName) {
  const endpoint = '/api/setCatalog';
  const response = await fetch(endpoint, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: `catalogName=${encodeURIComponent(catalogName)}`,
  });
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }
  console.log('Catalog updated successfully');
}

function updateOptions(select, options) {
  select.classList.add('loaded');

  select.innerHTML = "";
  if (!options || options.length === 0) {
    select.add(new Option("!NOTHING!", ""));
  } else {
    options.forEach(option => select.add(new Option(option, option)));
  }
}

async function displayTable(tableName) {
  const endpoint = `/api/toHtmlTable?tableName=${tableName}`;

  const response = await fetch(endpoint, {method: 'GET'});
  if (!response.ok) {
    console.error(new Error(`HTTP error! status: ${response.status}`))
  } else {
    tableView.innerHTML = await response.text();
    const tableCaption = document.querySelector(".tableCaption");
    tableCaption.innerText = `[${catalogDropdown.value}].[${schemaDropdown.value}].[${tableName}]`;

    console.log('Fetch call succeeded and HTML was injected.');
  }
}

async function onCatalogChange(catalogSchemasMap) {
  try {
    schemaDropdown.classList.remove('loaded');
    tableDropdown.classList.remove('loaded');
    viewDropdown.classList.remove('loaded');

    await updateCatalog(catalogDropdown.value);
    updateOptions(schemaDropdown, catalogSchemasMap[catalogDropdown.value]);
  } catch (error) {
    console.error('Failed to update catalog:', error);
  } finally {
    schemaDropdown.dispatchEvent(new Event("change"));
  }
}

async function onSchemaChange() {
  try {
    await updateSchema(schemaDropdown.value);

    const schemaTablesMap = await getSchemaTablesMap(catalogDropdown.value);
    updateOptions(tableDropdown, schemaTablesMap[schemaDropdown.value]);
    const schemaViewsMap = await getSchemaViewsMap(catalogDropdown.value);
    updateOptions(viewDropdown, schemaViewsMap[schemaDropdown.value]);
  } catch (error) {
    console.error('Failed to update schema:', error);
  }

  const dropdownToChange = tableDropdown.firstElementChild?.value ? tableDropdown : viewDropdown;
  dropdownToChange.dispatchEvent(new Event("change"));
}

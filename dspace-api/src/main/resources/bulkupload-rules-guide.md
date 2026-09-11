# Bulk Upload Rules Configuration Guide

The `bulkupload-rules.json` file defines validation rules for CSV columns during bulk upload. Each column in your CSV file should have a corresponding rule entry.

## Rule Properties

| Property        | Type      | Required | Default | Description |
|----------------|-----------|----------|---------|-------------|
| `name`         | String    | **Yes**  | -       | CSV column header name to match |
| `metadata`     | String    | No       | -       | DSpace metadata field (e.g., `dc.title`, `dc.contributor.author`) |
| `required`     | Boolean   | No       | `false` | If `true`, the column must have a non-empty value |
| `maxLength`    | Integer   | No       | -       | Maximum character length allowed for the value |
| `regex`        | String    | No       | -       | Java regex pattern the value must match |
| `dateFormat`   | String    | No       | -       | Date format pattern (e.g., `yyyy-MM-dd`) |
| `allowedValues`| Array     | No       | -       | List of allowed values (enum/dropdown) |
| `isCollection` | Boolean   | No       | `false` | If `true`, validates value as a DSpace collection handle or name |
| `isFile`       | Boolean   | No       | `false` | If `true`, validates value as an existing file path |
| `hasMultiple`  | Boolean   | No       | `false` | If `true`, value contains multiple values separated by `separator` |
| `separator`    | String    | No       | `\|\|`  | Separator used to split multiple values (used with `hasMultiple`) |

## How It Works

1. The rule engine loads `bulkupload-rules.json` on startup
   - If `bulkupload.rules.path` is set in `local.cfg` or `dspace.cfg`, loads from that filesystem path
   - Otherwise, loads from the classpath resource `bulkupload-rules.json`
2. For each CSV column header, it finds the matching rule:
   - First tries an exact match by `name`
   - If no exact match, checks if any `isFile: true` rule's `name` is a prefix of the column name (case-insensitive). e.g., CSV header `FileName` matches rule name `File`
3. During validation, each cell value is checked against all defined properties
4. During processing, the `metadata` field maps the CSV column to a DSpace metadata field
5. Columns with `isFile: true` are **never** treated as metadata — they are used only to create bitstreams

## Metadata Field Format

DSpace metadata fields use the format: `schema.element.qualifier`

- **schema**: e.g., `dc`, `local`, `loa`
- **element**: e.g., `title`, `contributor`, `date`
- **qualifier** (optional): e.g., `issued`, `author`, `iso`

Examples:
- `dc.title` → schema=`dc`, element=`title`
- `dc.contributor.author` → schema=`dc`, element=`contributor`, qualifier=`author`
- `dc.date.issued` → schema=`dc`, element=`date`, qualifier=`issued`

## Example Rules

### Simple text field
```json
{
  "name": "title",
  "metadata": "dc.title",
  "required": true,
  "maxLength": 500
}
```

### Required date field
```json
{
  "name": "date_issued",
  "metadata": "dc.date.issued",
  "required": true,
  "dateFormat": "yyyy-MM-dd"
}
```

### Field with allowed values (dropdown)
```json
{
  "name": "type",
  "metadata": "dc.type",
  "required": true,
  "allowedValues": ["LOA", "HR Policy"]
}
```

### Field with regex validation (e.g., 2-letter language code)
```json
{
  "name": "language",
  "metadata": "dc.language.iso",
  "required": false,
  "regex": "^[a-z]{2}$"
}
```

### Collection validation
```json
{
  "name": "collection",
  "metadata": "dc.relation.isPartOf",
  "required": true,
  "isCollection": true
}
```
The value should be a DSpace handle (e.g., `123456789/1`) or collection name.

### File path validation
```json
{
  "name": "File",
  "required": false,
  "isFile": true,
  "hasMultiple": true,
  "separator": "||"
}
```
The value should be a file name (not a full path). The base directory is configured via `bulkupload.file.basepath` in `local.cfg` or `dspace.cfg`.

- File names in the CSV are combined with the base path to locate files physically
- e.g., if `bulkupload.file.basepath=/opt/uploads` and CSV value is `abc.pdf`, the system looks for `/opt/uploads/abc.pdf`
- With `hasMultiple: true`, multiple files can be specified separated by `||`, e.g., `abc.pdf||def.pdf`
- Each file is uploaded as a separate bitstream in the ORIGINAL bundle
- After bitstream creation, the source file is moved to `bulkupload.file.processedpath` if configured
- The processed folder is automatically created if it does not exist (including parent directories)
- If a file with the same name already exists in the processed folder, a timestamp prefix is added to avoid overwriting
- CSV column headers like `FileName` will match this rule via intelligent prefix matching

### Multiple values in one cell
```json
{
  "name": "author",
  "metadata": "dc.contributor.author",
  "required": false,
  "maxLength": 200,
  "hasMultiple": true,
  "separator": "||"
}
```
CSV cell value: `John Smith||Jane Doe||Bob Lee`
This creates 3 separate `dc.contributor.author` metadata entries.

### Custom separator
```json
{
  "name": "keywords",
  "metadata": "dc.subject",
  "required": false,
  "hasMultiple": true,
  "separator": ","
}
```
CSV cell value: `science,technology,research`

### Numeric field with regex
```json
{
  "name": "page_number",
  "metadata": "dc.pagenumber",
  "required": false,
  "regex": "^[0-9]+$"
}
```

### Mobile number validation
```json
{
  "name": "mobile_number",
  "metadata": "dc.identifier.other",
  "required": false,
  "regex": "^\\+?[0-9]{10,15}$"
}
```

### Email validation
```json
{
  "name": "email",
  "metadata": "dc.identifier.email",
  "required": false,
  "regex": "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
}
```

### Field without metadata mapping (e.g., file path only)
```json
{
  "name": "File",
  "required": false,
  "isFile": true,
  "hasMultiple": true,
  "separator": "||"
}
```
No `metadata` property means the value is not stored as item metadata. File columns are used only to create bitstreams.

## Full Example

```json
{
  "columns": [
    {
      "name": "title",
      "metadata": "dc.title",
      "required": true,
      "maxLength": 500
    },
    {
      "name": "collection",
      "metadata": "dc.relation.isPartOf",
      "required": true,
      "isCollection": true
    },
    {
      "name": "File",
      "required": false,
      "isFile": true,
      "hasMultiple": true,
      "separator": "||"
    },
    {
      "name": "type",
      "metadata": "dc.type",
      "required": true,
      "allowedValues": ["LOA", "HR Policy"]
    },
    {
      "name": "date_issued",
      "metadata": "dc.date.issued",
      "required": true,
      "dateFormat": "yyyy-MM-dd"
    },
    {
      "name": "author",
      "metadata": "dc.contributor.author",
      "required": false,
      "maxLength": 200,
      "hasMultiple": true,
      "separator": "||"
    },
    {
      "name": "subject",
      "metadata": "dc.subject",
      "required": false,
      "maxLength": 200,
      "hasMultiple": true,
      "separator": "||"
    },
    {
      "name": "language",
      "metadata": "dc.language.iso",
      "required": false,
      "regex": "^[a-z]{2}$"
    },
    {
      "name": "publisher",
      "metadata": "dc.publisher",
      "required": false,
      "maxLength": 200
    },
    {
      "name": "department",
      "metadata": "dc.department",
      "required": false,
      "maxLength": 200
    },
    {
      "name": "category",
      "metadata": "dc.category",
      "required": false,
      "maxLength": 200
    },
    {
      "name": "description",
      "metadata": "dc.description",
      "required": false
    },
    {
      "name": "page_number",
      "metadata": "dc.pagenumber",
      "required": false,
      "regex": "^[0-9]+$"
    }
  ]
}
```

## CSV File Format

The CSV file must have a header row with column names matching the `name` property in the rules. File columns can also use names like `FileName` — the engine matches them intelligently. Example:

```csv
Department,Category,UDF1,Description,PageNumber,FileName
Admin,A,45RDH34,New invoice related to structure,12,abc.pdf
Civil,B||C,45RDH45,Purchase order related to structure,344,sample.pdf||sample1.pdf
```

In this example:
- `FileName` matches the `File` rule via prefix matching
- `abc.pdf` resolves to `{bulkupload.file.basepath}/abc.pdf`
- `sample.pdf||sample1.pdf` creates two bitstreams

## Validation Order

When a cell value is validated, rules are checked in this order:
1. **required** — checks if value is empty
2. **maxLength** — checks value length
3. **hasMultiple** — if true, splits by separator and validates each value individually
4. **regex** — checks if value matches pattern
5. **allowedValues** — checks if value is in the allowed list
6. **dateFormat** — checks if value is a valid date
7. **isCollection** — checks if value resolves to a DSpace collection (by handle or name, case-insensitive)
8. **isFile** — checks if file exists at `{bulkupload.file.basepath}/{filename}`. If `hasMultiple`, each file is validated individually

## Configuration Properties

| Property | Location | Description |
|----------|----------|-------------|
| `bulkupload.rules.path` | `local.cfg` or `dspace.cfg` | Filesystem path to the rules JSON file. If not set, defaults to classpath `bulkupload-rules.json` |
| `bulkupload.file.basepath` | `local.cfg` or `dspace.cfg` | Base directory path for files referenced in CSV. File names in CSV are resolved relative to this path |
| `bulkupload.file.processedpath` | `local.cfg` or `dspace.cfg` | Directory to move files after successful bitstream creation. If not set, files remain in the base path |

Example `local.cfg` entries:
```
bulkupload.rules.path=/dspace/config/bulkupload-rules.json
bulkupload.file.basepath=/dspace/bulkupload-files
bulkupload.file.processedpath=/dspace/bulkupload-processed
```

## Notes

- The `separator` default is `||` but can be any string (e.g., `,`, `;`, `|`)
- When `hasMultiple` is true, each split value is validated individually and added as a separate metadata entry (or a separate bitstream for file columns)
- The `metadata` field is optional — columns without it (like `File`) are not stored as item metadata
- Columns with `isFile: true` are **never** treated as metadata, even if the value is empty
- Multiple columns can map to the same DSpace metadata field (e.g., both `title` and `udf1` can map to `dc.title`)
- Regex patterns use Java regex syntax (remember to escape backslashes in JSON with `\\`)
- File columns support intelligent header matching: CSV header `FileName` matches rule name `File` (case-insensitive prefix match)
- Items created via bulk upload are automatically archived (installed) and indexed
- The result CSV includes `submitted` (true/false) and `message` columns showing per-row status
- Process statuses include: `PENDING`, `QUEUED`, `RUNNING`, `VALIDATED`, `VALIDATION_FAILED`, `COMPLETED`, `FAILED`, `CANCELLED`

## REST API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/core/bulk-upload/validate` | Upload a CSV file for validation and processing |
| `GET` | `/api/core/bulk-upload/process/{uuid}` | Get the status of a bulk upload process |
| `GET` | `/api/core/bulk-upload/process/{uuid}/result` | Download the result CSV for a completed process |
| `GET` | `/api/core/bulk-upload/sample-csv` | Download a sample CSV with only headers (based on loaded rules) |
| `GET` | `/api/core/bulk-upload/rules` | Download the current rules as a JSON file |
| `POST` | `/api/core/bulk-upload/rules` | Upload a new rules JSON file (saves to `bulkupload.rules.path` and reloads) |

All endpoints require `ADMIN` authority.

### Sample CSV

`GET /api/core/bulk-upload/sample-csv`

Returns a CSV file with only the header row, based on the currently loaded rules. Example response:
```csv
Department,Category,UDF1,Description,PageNumber,File
```

### Rules JSON

`GET /api/core/bulk-upload/rules`

Returns the currently loaded rules as a downloadable JSON file. This reflects the rules from `bulkupload.rules.path` (if configured) or the classpath `bulkupload-rules.json`.

### Upload Rules

`POST /api/core/bulk-upload/rules`

Uploads a new rules JSON file. The file is:
1. **Validated** as proper JSON
2. **Saved** to the path configured in `bulkupload.rules.path`
3. **Reloaded** into the rule engine immediately (no restart needed)

**Request**: `multipart/form-data` with a `file` parameter containing the JSON file.

**Prerequisite**: `bulkupload.rules.path` must be configured in `local.cfg` or `dspace.cfg`.

**Responses**:
- `200 OK` — `"Rules uploaded and reloaded successfully"`
- `400 Bad Request` — Invalid JSON or `bulkupload.rules.path` not configured
- `500 Internal Server Error` — Failed to save or reload rules

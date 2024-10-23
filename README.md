# Deploy

```bash
az deployment group create \
  --resource-group <resource-group> \
  --template-file <template-file>.json \
  --parameters imageRegistryUsername='<your-username>' \
               imageRegistryPassword='<your-password>'
```

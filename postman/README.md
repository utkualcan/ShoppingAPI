# Shopping API - Postman Test Collection

Bu klasörde Shopping API'nizi otomatik test etmek için Postman collection'ı bulunmaktadır.

## Kurulum ve Kullanım

### 1. Collection'ı İçe Aktarma
1. Postman'i açın
2. "Import" butonuna tıklayın
3. `ShoppingAPI_Collection.json` dosyasını seçin
4. Collection başarıyla içe aktarılacak

### 2. Otomatik Test Çalıştırma

#### Collection Runner ile:
1. Collection'a sağ tıklayın
2. "Run collection" seçin
3. "Run Shopping API - Automated Tests" butonuna tıklayın
4. Tüm testler sırayla çalışacak

#### Newman (CLI) ile:
```bash
# Newman kurulumu
npm install -g newman

# Testleri çalıştırma
newman run postman/ShoppingAPI_Collection.json
```

### 3. Test Senaryoları

Collection şu test akışını gerçekleştirir:

1. **User Management**
   - Yeni kullanıcı oluşturma
   - Kullanıcı bilgilerini getirme

2. **Product Management**
   - Yeni ürün oluşturma
   - Ürün bilgilerini getirme

3. **Cart Management**
   - Sepet oluşturma
   - Sepete ürün ekleme
   - Ürün miktarını güncelleme
   - Sepet bilgilerini getirme

4. **Cleanup**
   - Sepeti temizleme
   - Test verilerini silme

### 4. Otomatik Testler

Her request için otomatik kontroller:
- ✅ Status code kontrolü
- ✅ Response time kontrolü (< 2 saniye)
- ✅ Content-Type kontrolü
- ✅ Response data doğrulaması
- ✅ Business logic kontrolü

### 5. Environment Variables

Collection otomatik olarak şu değişkenleri yönetir:
- `baseUrl`: API base URL'i
- `userId`: Oluşturulan kullanıcı ID'si
- `productId`: Oluşturulan ürün ID'si
- `cartId`: Oluşturulan sepet ID'si

### 6. Sürekli Test Etme

Bu collection'ı CI/CD pipeline'ınıza entegre edebilirsiniz:

```yaml
# GitHub Actions örneği
- name: Run API Tests
  run: |
    npm install -g newman
    newman run postman/ShoppingAPI_Collection.json --reporters cli,json
```

### 7. Test Sonuçları

Newman çıktısında:
- ✅ Geçen testler yeşil
- ❌ Başarısız testler kırmızı
- 📊 Özet istatistikler

Bu şekilde her kod değişikliğinden sonra API'nizin düzgün çalıştığından emin olabilirsiniz.
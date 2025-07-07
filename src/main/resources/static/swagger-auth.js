/**
 * NIHAI VE EN GÜVENİLİR VERSİYON: Swagger UI Otomatik Yetkilendirme Script'i
 * Bu script, login endpoint'inin "Execute" butonuna doğrudan bir olay dinleyici ekler.
 * Bu, en hedefli ve sağlam yöntemdir.
 */
function setupSwaggerAutoAuth() {
    console.log("[Swagger Auto-Auth] Script başlatıldı. Arayüzün yüklenmesi bekleniyor...");

    const interval = setInterval(() => {
        // Login işleminin sarmalayıcısını bulmaya çalış
        const loginWrapper = document.querySelector("#operations-Authentication-authenticateUser");

        if (loginWrapper) {
            clearInterval(interval); // Arayüz hazır, beklemeyi durdur
            console.log("[Swagger Auto-Auth] Login endpoint'i bulundu. Olay dinleyici ekleniyor.");

            const executeBtn = loginWrapper.querySelector(".btn.execute");

            if (executeBtn) {
                executeBtn.addEventListener("click", function() {
                    // Yanıtın arayüzde render edilmesi için kısa bir süre bekle
                    setTimeout(extractTokenFromResponse, 750);
                });
                console.log("[Swagger Auto-Auth] 'Execute' butonuna olay dinleyici başarıyla eklendi.");
            } else {
                console.error("[Swagger Auto-Auth] Login 'Execute' butonu bulunamadı!");
            }
        }
    }, 250); // Her 250 milisaniyede bir kontrol et
}

function extractTokenFromResponse() {
    console.log("[Swagger Auto-Auth] Login yanıtı kontrol ediliyor...");

    // Login yanıtının gösterildiği HTML elementini bul
    const responseBodyPre = document.querySelector("#operations-Authentication-authenticateUser .responses-wrapper .response-col_description .highlight-code .microlight");

    if (responseBodyPre && responseBodyPre.innerText) {
        try {
            const responseData = JSON.parse(responseBodyPre.innerText);
            if (responseData && responseData.token) {
                const token = responseData.token;
                console.log("BAŞARILI: Token yakalandı! Swagger UI otomatik olarak yetkilendiriliyor.");

                // 'bearerAuth' isminin OpenApiConfig.java dosyanızdaki ile aynı olduğundan emin olun
                window.ui.preauthorizeApiKey("bearerAuth", "Bearer " + token);
            } else {
                console.warn("[Swagger Auto-Auth] Login yanıtı alındı ancak içinde 'token' alanı bulunamadı.");
            }
        } catch (e) {
            console.error("[Swagger Auto-Auth] HATA: Login yanıtı JSON olarak ayrıştırılamadı.", e);
        }
    } else {
        console.warn("[Swagger Auto-Auth] Login yanıt gövdesi henüz bulunamadı. Tekrar denenecek...");
        // Bazen render daha uzun sürebilir, bir kez daha dene
        setTimeout(extractTokenFromResponse, 1000);
    }
}

// Script'i başlat
window.addEventListener('load', setupSwaggerAutoAuth);
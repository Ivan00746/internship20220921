package org.example.entity.shop;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;

@Data
public class PaymentOrderRequest {
    @NonNull
    private Paymentorder paymentorder;

    @Data
    public static class Paymentorder {
        private String operation;//"Purchase"
        private String currency;//"SEK"
        private int amount;//1500
        private int vatAmount;//375
        private String description;//"Test Purchase"
        private String userAgent;// "Mozilla/5.0"
        private String language;//"sv-SE"
    //        private boolean generateRecurrenceToken;
        private String productName;// "Checkout3"
        @NonNull
        private Urls urls;
        @NonNull
        private PayeeInfo payeeInfo;
        @NonNull
        private Payer payer;
        @NonNull
        private ArrayList<OrderItems> orderItems;
        @NonNull
        private RiskIndicator riskIndicator;
        //Этот необязательный объект состоит из информации, которая помогает проверить плательщика.
        //Предоставление этих полей снижает вероятность того, что плательщику придется запрашивать
        // аутентификацию 3-D Secure 2.0 при аутентификации покупки.

        @Data
        public static class Urls {
            private String[] hostUrls;// [ "https://example.com", "https://example.net" ]
            //Массив URL-адресов, допустимых для встраивания бесшовных просмотров Swedbank Pay.
            private String completeUrl;// "https://example.com/payment-completed"
            //URL-адрес, на который Swedbank Pay перенаправит обратно, когда плательщик
            // завершит взаимодействие с платежом. Это не указывает на успешный платеж,
            // а только на то, что он достиг конечного (завершенного) состояния.
            // Для дальнейшей проверки платежного поручения необходимо выполнить запрос GET.
            // Подробнее см. в разделе completeUrl.
            private String cancelUrl;// "https://example.com/payment-cancelled"
            //URL-адрес для перенаправления плательщика в случае отмены платежа либо плательщиком,
            // либо продавцом посредством запроса отмены платежа или платежного поручения.
            // URL-адрес для перенаправления плательщика в случае отмены платежа либо плательщиком,
            // либо продавцом посредством запроса отмены платежа или платежного поручения.
            private String callbackUrl;// "https://api.example.com/payment-callback"
            //URL-адрес конечной точки API, получающей POST-запросы о транзакции,
            // связанной с платежным поручением.
            private String termsOfServiceUrl;// "https://example.com/termsandconditions.pdf"
            //URL-адрес документа с условиями обслуживания, который плательщик должен
            // принять для завершения платежа. HTTPS является обязательным требованием.
        }

        @Data
        public static class PayeeInfo {
            private String payeeId;//"5cabf558-5283-482f-b252-4d58e06f6f3b"
            private String payeeReference;// "AB832"
            // Уникальная ссылка из торговой системы. Установите для каждой операции,
            // чтобы обеспечить однократную доставку транзакционной операции.
            // Проверка длины и содержимого зависит от того, отправляется ли эквайеру
            // transaction.number или payeeReference. Если расчетом занимается Swedbank Pay,
            // отправляется transaction.number, а payeeReference должен быть
            // в формате A-Za-z0-9 (включая -) и строку (30). Если расчет осуществляете вы,
            // Swedbank Pay отправит ссылку получателя платежа в формате строки (12).
            // Все символы должны быть цифрами. В Платежах по счетам payeeReference
            // используется как номер счета-фактуры/квитанции, если receiptReference
            // не определена.
            private String payeeName;//"Merchant1"
            //Имя получателя платежа, обычно имя продавца.
            private String productCategory;//"A123"
            //Категория продукта или номер, отправленный получателем платежа/продавцом.
            // Это не подтверждается Swedbank Pay, но будет передано в процессе оплаты и
            // может быть использовано в процессе расчета.
            private String orderReference;//"or-123456"
            //Ссылка на заказ должна отражать ссылку на заказ, найденную в системах продавца.
            private String subsite;//"MySubsite"
            //Поле дочернего сайта можно использовать для выполнения раздельного расчета платежа.
            // Дочерние сайты должны быть разрешены с помощью reconciliation Swedbank Pay перед
            // использованием.
            private String siteId;//"MySiteId"
        }

        @Data
        public static class RiskIndicator {
            private String deliveryEmailAddress;// "olivia.nyhuus@payex.com"
            //Для электронной доставки адрес электронной почты, на который был доставлен товар.
            //Предоставление этого поля, когда это уместно, снижает вероятность аутентификации
            //3-D Secure для плательщика.
            private String deliveryTimeframeIndicator;// "01"
            //Indicates the merchandise delivery timeframe.
            //01 (Electronic Delivery)
            //02 (Same day shipping)
            //03 (Overnight shipping)
            //04 (Two-day or more shipping)
            private String preOrderDate;// "19801231"
            //Для предзаказанной покупки. Ожидаемая дата, когда товар будет доступен.
            // Формат: ГГГГММДД
            private String preOrderPurchaseIndicator;// "01"
            //Указывает, размещает ли плательщик заказ на товары с будущей доступностью
            //или датой выпуска.
            //01 (товар доступен)
            //02 (Будет доступно в будущем)
            private String shipIndicator;// "01"
            //Indicates shipping method chosen for the transaction.
            //01 (Ship to cardholder’s billing address)
            //02 (Ship to another verified address on file with merchant)
            //03 (Ship to address that is different than cardholder’s billing address)
            //04 (Ship to Store / Pick-up at local store. Store address shall be populated in shipping address fields)
            //05 (Digital goods, includes online services, electronic giftcards and redemption codes)
            //06 (Travel and Event tickets, not shipped)
            //07 (Other, e.g. gaming, digital service)
            private boolean giftCardPurchase;// false
            //true if this is a purchase of a gift card.
            private String reOrderPurchaseIndicator;// "01"
            //Indicates whether the cardholder is reordering previously purchased merchandise.
            //01 (First time ordered)
            //02 (Reordered).
            @NonNull
            private PickUpAddress pickUpAddress;
            //Если для параметра shipIndicator установлено значение 04,
            // предварительно заполните его адресом pickUpAddress плательщика для покупки,
            // чтобы уменьшить фактор риска покупки.

            @Data
            public static class PickUpAddress {
                private String name;// "Olivia Nyhus"
                private String streetAddress;// "Saltnestoppen 43"
                private String coAddress;// ""
                private String city;// "Saltnes"
                private String zipCode;// "1642"
                private String countryCode;// "NO"
            }
        }
    }
}

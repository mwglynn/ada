import org.junit.Test;
import org.junit.Assert;

import java.util.Optional;

public class Messages_inputLong_Test {

    private static final int port = 6259;

    @Test
    public void messagesLength() {
        TCPHost host = new TCPHost(port);

        Thread hostThread =
                new Thread(
                        () -> {
                            //noinspection StatementWithEmptyBody
                            while (host.Tick()) {
                            }
                            host.Close();
                        });
        hostThread.start();

        NetworkSocketClient client1 = new NetworkSocketClient("localhost", port);
        NetworkSender sender1 = new NetworkSender(client1);
        NetworkSocketClient client2 = new NetworkSocketClient("localhost", port);
        NetworkReader reader2 = new NetworkReader(client2);

        String longMessage = "" +
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAASwAAACWCAYAAABkW7XSAAAG0klEQVR4X" +
                "u3dTYjtcxzH8c+VEopYoDyUlJ2HKBRZsLCQp53CQtl4Kiyk7EgRGw8lG12JhZWEUuQhkiSUsvJQimQh" +
                "kqcF+tW5NU13xjln7tzz/5z7mo3NOed+5/X99m5m7rljT3wQIECgRGBPyZzGJECAQATLERAgUCMgWDW" +
                "rMigBAoLlBggQqBGYUrBOS3JJktNnel8neT/JdzWaBiVAYFcFphCsc5I8lOTUJK8lGaEaH2ckuTLJN0" +
                "nuT/LFrkp4cQIEJi8wgnV5kpt2cdIHk3y1xevfkuSuJHckeWeLx1yW5MkkjybZu4tzemkCBCYuMIJ1e" +
                "5KndnHOi5J8tJ/XvybJPUmuSvLr//z5xyZ5Nckjs//u4rhemgCBqQqsKlgnzr6iGl89/TAnzslJ3kxy" +
                "aZKf5nyOhxEgsEYCm4P1ZZIXDsDnd2+SY2avs7+vsMbPpMZXVeNbvUU+7k5yRJKHF3mSxxIgsB4Cm4P" +
                "1cpLrDsCnNv5m75RtgjV+gH5xkl8W/LOOS/JukrMXfJ6HEyCwBgKrCNZRs7crnLek3+dJLkjy15LP9z" +
                "QCBEoFVhGsM5M8luTqJc1en/2t4r63Pyz5Mp5GgECbwCqCdXySt5OM918t8/FZkgt9hbUMnecQ6BZYR" +
                "bCG2I9JxjvbF/227sgkHyY5t5vd9AQILCOwqmA9k+SlJG8tOPQVSa5PcvOCz/NwAgTWQGBVwTprFqzx" +
                "g/c/5nQ8OskHSa5N8u2cz/EwAgTWSGBVwRqEDyQZb1O4c07Pp5OM94k9MefjPYwAgTUTWGWwDkvyYpL" +
                "Dk9y6zbvXT0gyvoX8fha3f9ZsBz4dAgTmFFhlsPaNOGI13sH+XJJPknyaZMRs/GD9/CQ3JrkvyXhTqw" +
                "8CBA5hgSkEa/CPn0/dMPt3guMd8H8neWX2BtP3kvx8CO/Ip06AwExgKsHauJDx7wx/S/K4LREgQGCjg" +
                "GC5BwIEagQEq2ZVBiVAYHOw3kgyfgvoTj8+TnLS7EW2+gV+W/0ZviXcqb7nE1hTgVX9Ar/tOAVrTY/N" +
                "p0VgpwKCtVNBzydA4KAJrCpYz27z62XGP3D+N8mfWyg8P3vf1kFD8gcRIDANgSn8b742S/iWcBq3YQo" +
                "CkxMQrMmtxEAECGwlIFhugwCBGgHBqlmVQQkQECw3QIBAjYBg1azKoAQITDFYtyX5Pcle6yFAgMBGgS" +
                "kGy4YIECCwXwHBchgECNQICFbNqgxKgIBguQECBGoEBKtmVQYlQECw3AABAjUCglWzKoMSICBYboAAg" +
                "RoBwapZlUEJEBAsN0CAQI2AYNWsyqAECAiWGyBAoEZAsGpWZVACBATLDRAgUCMgWDWrMigBAoLlBggQ" +
                "qBEQrJpVGZQAAcFyAwQI1AgIVs2qDEqAgGC5AQIEagQEq2ZVBiVAQLDcAAECNQKCVbMqgxIgIFhugAC" +
                "BGgHBqlmVQQkQECw3QIBAjYBg1azKoAQICJYbIECgRkCwalZlUAIEBMsNECBQIyBYNasyKAECguUGCB" +
                "CoERCsmlUZlAABwXIDBAjUCAhWzaoMSoCAYLkBAgRqBASrZlUGJUBAsNwAAQI1AoJVsyqDEiAgWG6AA" +
                "IEaAcGqWZVBCRAQLDdAgECNgGDVrMqgBAgIlhsgQKBGQLBqVmVQAgQEyw0QIFAjIFg1qzIoAQKC5QYI" +
                "EKgREKyaVRmUAAHBcgMECNQICFbNqgxKgIBguQECBGoEBKtmVQYlQECw3AABAjUCglWzKoMSICBYboA" +
                "AgRoBwapZlUEJEBAsN0CAQI2AYNWsyqAECAiWGyBAoEZAsGpWZVACBATLDRAgUCMgWDWrMigBAoLlBg" +
                "gQqBEQrJpVGZQAAcFyAwQI1AgIVs2qDEqAgGC5AQIEagQEq2ZVBiVAQLDcAAECNQKCVbMqgxIgIFhug" +
                "ACBGgHBqlmVQQkQECw3QIBAjYBg1azKoAQICJYbIECgRkCwalZlUAIEBMsNECBQIyBYNasyKAECguUG" +
                "CBCoERCsmlUZlAABwXIDBAjUCAhWzaoMSoCAYLkBAgRqBASrZlUGJUBAsNwAAQI1AoJVsyqDEiAgWG6" +
                "AAIEaAcGqWZVBCRAQLDdAgECNgGDVrMqgBAgIlhsgQKBGQLBqVmVQAgQEyw0QIFAjIFg1qzIoAQKC5Q" +
                "YIEKgREKyaVRmUAAHBcgMECNQICFbNqgxKgIBguQECBGoEBKtmVQYlQECw3AABAjUCglWzKoMSICBYb" +
                "oAAgRoBwapZlUEJEBAsN0CAQI2AYNWsyqAECAiWGyBAoEZAsGpWZVACBATLDRAgUCMgWDWrMigBAoLl" +
                "BggQqBEQrJpVGZQAAcFyAwQI1Aj8B6OmqZcZSyd8AAAAAElFTkSuQmCC";

        Thread sendMessages =
                new Thread(
                        () -> sender1.SendMessage(longMessage));
        sendMessages.start();

        Optional<String> s2;
        do {
            s2 = reader2.ReadMessage();
            if (s2.isPresent()) {
                Assert.assertEquals(longMessage, s2.get());
                break;
            }
        } while (true);


        sendMessages.interrupt();
        hostThread.interrupt();
//        System.exit(0);

    }

}

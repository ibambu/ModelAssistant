/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.util.text;

/**
 *
 * @author luotao
 */
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;

public class FileCharsetDetector {

    private boolean found = false;
    private String encoding = null;

    public static void main(String[] argv) throws Exception {
        File file1 = new File("/home/luotao/work/project/jchardet/data/1.txt");

        System.out.println("�ļ�����:" + new FileCharsetDetector().guessFileEncoding(file1));
    }

    /**
     * ����һ���ļ�(File)���󣬼���ļ�����
     *
     * @param file File����ʵ��
     * @return �ļ����룬���ޣ��򷵻�null
     * @throws FileNotFoundException
     * @throws IOException
     */
    public String guessFileEncoding(File file) throws FileNotFoundException, IOException {
        return guessFileEncoding(file, new nsDetector());
    }

    /**
     * <pre>
     * ��ȡ�ļ��ı���
     * @param file
     *            File����ʵ��
     * @param languageHint ������ʾ������� @see #nsPSMDetector ,ȡֵ���£� 1 : Japanese 2 :
     * Chinese 3 : Simplified Chinese 4 : Traditional Chinese 5 : Korean 6 :
     * Dont know(default)
     * </pre>
     *
     * @return �ļ����룬eg��UTF-8,GBK,GB2312��ʽ(��ȷ����ʱ�򣬷��ؿ��ܵ��ַ���������)�����ޣ��򷵻�null
     * @throws FileNotFoundException
     * @throws IOException
     */
    public String guessFileEncoding(File file, int languageHint) throws FileNotFoundException, IOException {
        return guessFileEncoding(file, new nsDetector(languageHint));
    }

    /**
     * ��ȡ�ļ��ı���
     *
     * @param file
     * @param det
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private String guessFileEncoding(File file, nsDetector det) throws FileNotFoundException, IOException {
        // Set an observer...
        // The Notify() will be called when a matching charset is found.
        det.Init(new nsICharsetDetectionObserver() {
            public void Notify(String charset) {
                encoding = charset;
                found = true;
            }
        });

        BufferedInputStream imp = new BufferedInputStream(new FileInputStream(file));
        byte[] buf = new byte[1024];
        int len;
        boolean done = false;
        boolean isAscii = false;

        while ((len = imp.read(buf, 0, buf.length)) != -1) {
            // Check if the stream is only ascii.
            isAscii = det.isAscii(buf, len);
            if (isAscii) {
                break;
            }
            // DoIt if non-ascii and not done yet.
            done = det.DoIt(buf, len, false);
            if (done) {
                break;
            }
        }
        imp.close();
        det.DataEnd();

        if (isAscii) {
            encoding = "ASCII";
            found = true;
        }

        if (!found) {
            String[] prob = det.getProbableCharsets();
            //���ｫ���ܵ��ַ��������������
            for (int i = 0; i < prob.length; i++) {
                if (i == 0) {
                    encoding = prob[i];
                } else {
                    encoding += "," + prob[i];
                }
            }

            if (prob.length > 0) {
                // ��û�з��������,Ҳ����ֻȡ��һ�����ܵı���,���ﷵ�ص���һ�����ܵ�����
                return encoding;
            } else {
                return null;
            }
        }
        return encoding;
    }
}

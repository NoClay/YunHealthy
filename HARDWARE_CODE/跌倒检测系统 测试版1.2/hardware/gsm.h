#ifndef GSM_H
#define GSM_H

//Sms Service Center Number�����ŷ������ĺ��룬������13800290500���������86����Ϊ�ַ�����ʽ
#define GSM_SSCN "8613800290500"
//Addressee Number�����˺��룬�������86����Ϊ�ַ�����ʽ
//#define GSM_AN "8618829291175"
#define GSM_AN "8618829289875"

//extern const char GSM_SSCN[13];
//extern const char GSM_AN[13];
//const char GSM_SSCN[13] = "8613800290500";
//const char GSM_AN[13] = "8618829291175";

extern char ServiceCenterNumber[15];//���ŷ������ĺ����ܳ�14�����һλ�š�\0��
extern char AddresseeNumber[15];//�����˺����ܳ�14�����һλ�š�\0��
extern char LocationMessage[34];//��λ���ţ������ܳ�33�����һλ�š�\0��

unsigned char GSMReady(void);
unsigned char GSMSleep(void);
unsigned char GSMWakeup(void);

void ChangeNumber(char *RawNum, char *AimNum);//����ԭ�����ŵ������ת�����ź��������
void SendGSMCommand(char *p);//����\r\n������ʱҪע�⣡����
void SendLocationMessage(void);
void SendFallDownMessage(void);
void SendAlarmMessage(void);

#endif

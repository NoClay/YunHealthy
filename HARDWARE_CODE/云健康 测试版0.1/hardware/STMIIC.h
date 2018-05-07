#ifndef STMIIC_C
#define STMIIC_C

//#include "sys.h"

//���һλ0Ϊд��1Ϊ��

//����I2C����
//sbit SCL = P1^0;//ʱ����
//sbit SDA = P1^1;//������

/*
#define SDA_IN()  {GPIOC->CRH &= 0XFFFF0FFF;GPIOC->CRH |= 8<<12;}
#define SDA_OUT() {GPIOC->CRH &= 0XFFFF0FFF;GPIOC->CRH |= 3<<12;}


#define SDAI PCin(11)//�����ߣ�������
#define SDAO PCout(11)//�����ߣ������
#define SCL PCout(12)//ʱ���ߣ�������ó������ʱ���߶�������������
*/



//���һ��iic�Ľӿڼ�ֱ����������
#define SDA_IN()  {GPIOB->CRL &= 0XFFFFFFF0;GPIOB->CRL |= 0x00000008;}
#define SDA_OUT() {GPIOB->CRL &= 0XFFFFFFF0;GPIOB->CRL |= 0x00000003;}

#define SDAI PBin(0)//�����ߣ�������
#define SDAO PBout(0)//�����ߣ������
#define SCL PBout(1)//ʱ���ߣ�������ó������ʱ���߶�������������



void IICdelay(void);//������ʱ
void IICinit(void);//���߳�ʼ����initialise
void IICstart(void);//������ʼ�ź�
void IICstop(void);//����ֹͣ�ź�
unsigned char SlaRes(void);//���豸Ӧ��slave respons
void MasRes(void);//���豸Ӧ��master respons
void NoRes(void);//���豸��Ӧ��no respons
void IICwrite(unsigned char iicdata);//дһ���ֽ�
unsigned char IICread(void);//��һ���ֽ�
void IICWriteOneByte(unsigned char DeviceAddress, unsigned char RegisterAddress, unsigned char dat);//��I2C�豸��дһ���ֽ����ݣ�WriteOneByte
unsigned char IICReadOneByte(unsigned char DeviceAddress,unsigned char RegisterAddress);//��I2C�豸��ȡһ���ֽ�����

#endif

#ifndef GPS_H
#define GPS_H

extern char LongtitudeDirection, LatitudeDirection;
extern double longtitude, latitude;

double ChangeCoord(double data);//ת����γ��
unsigned char getgps(char *p);//��λ�ɹ�����1����λʧ�ܷ���0
void MakeLocationMessage(void);

#endif

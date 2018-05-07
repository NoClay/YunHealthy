#ifndef GPS_H
#define GPS_H

extern char LongtitudeDirection, LatitudeDirection;
extern double longtitude, latitude;

double ChangeCoord(double data);//转换经纬度
unsigned char getgps(char *p);//定位成功返回1，定位失败返回0
void MakeLocationMessage(void);

#endif

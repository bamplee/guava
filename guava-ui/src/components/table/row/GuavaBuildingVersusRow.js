import React from 'react'
import {useHistory} from 'react-router-dom';
import {useRecoilState} from 'recoil';
import {Badge} from 'antd-mobile/lib/index';

import {areaTypeState,} from '../../datatool/state';

import classNames from 'classnames/bind';
import styles from '../guavaTable.module.scss';
import CloseOutlined from '@ant-design/icons/es/icons/CloseOutlined';
import {CHART_COLOR_LIST} from '../../constant';

const cx = classNames.bind(styles);

const GuavaBuildingVersusRow = ({page, idx, building, handleClick}) => {
    const history = useHistory();
    const [areaType, setAreaType] = useRecoilState(areaTypeState);

    const getAreaList = (areaList) => {
        let area = areaList.map(x => x.name.replace('평', '') * 1).map(n => Math.floor(n / 10) * 10).sort();
        let startArea = area[0];
        let endArea = area[area.length - 1];
        let name = '-';
        if (startArea === endArea) {
            if (startArea) {
                name = `${startArea}평대`;
            }
        } else {
            name = `${startArea}~${endArea}평`;
        }
        return (
            <div>
                <div>
                    {name}
                </div>
            </div>
        )
    };

    return (
        building &&
        <div groupKey={page} key={page + '-' + idx}
             className={cx('column')}>
            <div className={cx('row', 'apt_name')}
                 style={{
                     backgroundColor: `${CHART_COLOR_LIST[idx]}`,
                     borderRadius: 2,
                     color: '#fff',
                     marginRight: 3,
                     marginLeft: 3,
                     paddingRight: 2,
                     paddingLeft: 2,
                     textAlign: 'center'
                 }}
                 onClick={() => {
                     building.type === 'BUILDING' ?
                     history.push('/b/' + building.buildingId) :
                     history.push('/r/' + building.id)
                 }}>
                {building.name}
            </div>
            <div className={cx('row')}>
                <div>{building.sinceYear ? building.sinceYear + '년차' : '-'}</div>
            </div>
            <div className={cx('row')}>
                <div>{building.floorAreaRatio ? building.floorAreaRatio + '%' : '-'}</div>
            </div>
            <div className={cx('row')}>
                <div>{building.hoCount ? building.hoCount + '세대' : '-'}</div>
            </div>
            <div className={cx('row')}>{building.areaList && getAreaList(building.areaList)}</div>
            {
                idx !== 0 ?
                    <div className={cx('row', 'cancel')}
                         onClick={() => handleClick(building)}><CloseOutlined/></div> :
                    <div className={cx('row', 'cancel')}></div>
            }
        </div>
    )
};

export default GuavaBuildingVersusRow;